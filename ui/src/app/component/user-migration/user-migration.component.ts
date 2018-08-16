import {Component} from '@angular/core';
import {PapaParseService} from "ngx-papaparse";
import {AppComponent} from "../app/app.component";
import {UserService} from "../../service/user.service";
import {flatMap} from "rxjs/operators";
import {ErrorInterceptor} from "../../interceptor/error.interceptor";
import {Router} from "@angular/router";

@Component({
  selector: 'user-migration',
  templateUrl: './user-migration.component.html'
})
export class UserMigrationComponent {
  processing: boolean;
  processedCount: number = 0;
  aliases: Alias[];

  constructor(private papa: PapaParseService, private userService: UserService, public router: Router) {}

  readFile(file: File): void {
    if (file == null) return;

    AppComponent.info("Parsing file " + file.name + "...");
    this.processing = true;

    this.papa.parse(file, {
      complete: results => {
        if (results.data.length <= 1) {
          return this.abort("No rows found. Please check the file " + file.name);
        }

        this.aliases = [];
        for (let i = 1; i < results.data.length; i++) {
          let row = results.data[i];
          if (row.length == 0 || row[0] === "") continue;
          if (row.length != 2) {
            return this.abort("More than 2 columns detected at row " + (i+1) + ". Please correct the file and try again.");
          }

          this.aliases.push({
            username: row[0],
            alias: row[1]
          });
        }

        AppComponent.hideMessage();
        this.processing = false;
      },
      error: e => this.abort("Unable to parse file: " + e)
    });
  }

  updateUsers(): void {
    this.processing = true;
    this.processedCount = 0;

    for (let i = 0; i < this.aliases.length; i++) {
      this.process(this.aliases[i])
    }
  }

  process(alias: Alias): void {
    alias.processing = true;
    alias.processed = false;
    alias.error = null;

    this.userService.read(alias.username)
      .pipe(flatMap(user => this.userService.update({
        ...user,
        aliasUsername: alias.alias
      })))
      .subscribe(() => {
        alias.processed = true;
        this.done(alias, ++this.processedCount);
      }, (error) => {
        alias.processed = false;
        alias.error = ErrorInterceptor.parseErrorResponse(error);
        this.done(alias, ++this.processedCount);
      });
  }

  done(alias: Alias, count: number): void {
    alias.processing = false;
    if (count >= this.aliases.length) {
      this.processing = false;
      let errors = this.aliases.map(a => a.error).filter(error => error != null);
      if (errors.length > 0) {
        AppComponent.error("Creation of " + errors.length + "/" + this.aliases.length + " alias" + (this.aliases.length != 1? "es": "") + " failed. See below for details.");
      } else {
        AppComponent.success("Created " + this.aliases.length + " alias" + (this.aliases.length != 1? "es": "") + " successfully.")
      }
      window.scrollTo(0, 0);
    }
  }

  abort(message: string): void {
    AppComponent.error(message);
    this.aliases = null;
    this.processing = false;
    window.scrollTo(0, 0);
  }
}


class Alias {
  username: string;
  alias: string;
  processing?: boolean = false;
  processed?: boolean = false;
  error?: string = null;
}

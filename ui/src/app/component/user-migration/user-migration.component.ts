import {Component} from '@angular/core';
import {PapaParseService} from "ngx-papaparse";
import {AppComponent} from "../app/app.component";
import {ErrorInterceptor} from "../../interceptor/error.interceptor";
import {Router} from "@angular/router";
import {Alias} from "../../model/alias";
import {AliasService} from "../../service/alias.service";

@Component({
  selector: 'user-migration',
  templateUrl: './user-migration.component.html'
})
export class UserMigrationComponent {
  private batchInterval: number;
  processing: boolean;
  processedCount: number = 0;

  aliases: AliasRequest[];

  constructor(private papa: PapaParseService, private aliasService: AliasService, public router: Router) {}

  readFile(file: File): void {
    if (file == null) return;

    AppComponent.info("Parsing file " + file.name + "...");
    this.processing = true;

    this.papa.parse(file, {
      complete: results => {
        if (results.data.length <= 1) {
          console.error("Invalid data", results);
          return this.abort("No data found. Please check the file " + file.name);
        }

        this.aliases = [];
        for (let i = 1; i < results.data.length; i++) {
          let row = results.data[i];
          if (row.length == 0 || row[0] === "") continue;
          if (row.length != 2) {
            console.error("Invalid row", row);
            return this.abort("More than 2 columns detected at row " + (i+1) + ". Please correct the file and try again.");
          }

          this.aliases.push({
            username: row[0],
            aliasUsername: row[1]
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

    this.processBatch(0);
  }

  private processBatch(batchNumber: number): void {
    if (this.processing == false) return;

    let batchSize: number = 10;
    let batchStart = batchNumber * batchSize;
    let batchEnd = (batchNumber + 1) * batchSize;
    for (let i = batchStart; i < batchEnd; i++) {
      this.process(this.aliases[i]);
    }

    this.batchInterval = setInterval(() => {
      if (this.processedCount >= batchEnd) {
        clearInterval(this.batchInterval);
        this.processBatch(batchNumber + 1);
      }
    }, 500);
  }

  private process(alias: AliasRequest): void {
    alias.processing = true;
    alias.processed = false;
    alias.error = null;

    this.aliasService.update(alias)
      .subscribe(() => {
        alias.processed = true;
        this.done(alias, ++this.processedCount);
      }, (error) => {
        alias.processed = false;
        alias.error = ErrorInterceptor.parseErrorResponse(error);
        this.done(alias, ++this.processedCount);
      });
  }

  private done(alias: AliasRequest, count: number): void {
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

  private abort(message: string): void {
    AppComponent.error(message);
    this.aliases = null;
    this.processing = false;
    window.scrollTo(0, 0);
  }
}

class AliasRequest extends Alias {
  processing?: boolean = false;
  processed?: boolean = false;
  error?: string = null;
}

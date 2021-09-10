// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import {AuthConfig} from 'angular-oauth2-oidc';

const authConfig: AuthConfig = {
  clientId: 'UserManagement-UI',
  dummyClientSecret: '',
  scope:
    // User management interactions (eg. add/update user):
    'UMBI001 UMBI002 UMBI003 UMBI004 UMBI005 UMBI006 UMBI007 UMBI008 UMBI009 UMBI010 UMBI011 UMBI012 ' +
    // Role management interactions (eg. public/private/national admin):
    'UABT0050 UABI020 UABI021 UABI022 UABI023 UABI024 UABI025 UABI026',
  resource: 'NDelius',
  responseType: 'code',
  useHttpBasicAuth: true,
  oidc: false,
  requireHttps: false,
  loginUrl: 'http://localhost:8080/umt/oauth/authorize',
  tokenEndpoint: 'http://localhost:8080/umt/oauth/token',
  redirectUri: window.location.origin + '/umt/',
};

export const environment = {
  production: false,
  api: {
    baseurl: 'http://localhost:8080/umt/api/'
  },
  authConfig: authConfig
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.

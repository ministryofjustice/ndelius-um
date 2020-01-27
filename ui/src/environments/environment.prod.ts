import {AuthConfig} from 'angular-oauth2-oidc';

const authConfig: AuthConfig = {
  clientId: 'UserManagement-UI',
  dummyClientSecret: '',
  scope:
    // User management interactions (eg. add/update user):
    'UMBI001 UMBI002 UMBI003 UMBI004 UMBI005 UMBI006 UMBI007 UMBI008 UMBI009 UMBI010 UMBI011 ' +
    // Role management interactions (eg. public/private/national admin):
    'UABI020 UABI021 UABI022 UABI023 UABI024 UABI025 UABI026',
  resource: 'NDelius',
  responseType: 'code',
  useHttpBasicAuth: true,
  oidc: false,
  requireHttps: false,
  loginUrl: '/umt/oauth/authorize',
  tokenEndpoint: '/umt/oauth/token',
  redirectUri: '/umt/',
};

export const environment = {
  production: true,
  api: {
    baseurl: '/umt/api/'
  },
  authConfig: authConfig
};

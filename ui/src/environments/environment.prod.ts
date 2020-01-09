import {AuthConfig} from 'angular-oauth2-oidc';

const authConfig: AuthConfig = {
  clientId: 'UserManagement-UI',
  dummyClientSecret: '',
  scope: 'UMBI001 UMBI002 UMBI003 UMBI004 UMBI005 UMBI006 UMBI007 UMBI008 UMBI009 UMBI010 UMBI011',
  resource: 'oauth-resource',
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

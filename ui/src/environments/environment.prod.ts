import {AuthConfig} from 'angular-oauth2-oidc';

const authConfig: AuthConfig = {
  clientId: 'UserManagement-UI',
  dummyClientSecret: '',
  scope: '', // request all user management interactions
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

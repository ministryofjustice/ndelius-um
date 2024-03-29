const url = "http://localhost:8080/umt";
const login = [
  `navigate to ${url}`,
  "set field #username to test.user",
  "set field #password to secret",
  "click element button",
  "wait for #current-user-link to be visible"
];

module.exports = {
  defaults: {
    chromeLaunchConfig: {
      args: [
        "--no-sandbox",
        "--disable-setuid-sandbox",
        "--disable-dev-shm-usage"
      ]
    },
    timeout: 10000,
    useIncognitoBrowserContext: true,
  },
  urls: [
    {
      url: `${url}/search`,
      actions: [
        ...login,
        `navigate to ${url}/search`,
        "wait for element #query to be visible"
      ]
    },
    {
      url: `${url}/user`,
      actions: [
        ...login,
        `navigate to ${url}/user`,
        "wait for element #username to be visible"
      ]
    }
  ]
};

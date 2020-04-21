const url = "http://localhost:4200/umt";
const login = [
  `navigate to ${url}`,
  "screen capture dist/pa11y-login.png",
  "set field #username to test.user",
  "set field #password to secret",
  "click element button",
  "wait for #current-user-link to be visible"
];

module.exports = {
  defaults: {
    useIncognitoBrowserContext: true
  },
  urls: [
    {
      url: `${url}/search`,
      screenCapture: "dist/pa11y-search.png",
      actions: [
        ...login,
        `navigate to ${url}/search`,
        "wait for element #query to be visible"
      ]
    },
    {
      url: `${url}/user`,
      screenCapture: "dist/pa11y-user.png",
      actions: [
        ...login,
        `navigate to ${url}/user`,
        "wait for element #username to be visible"
      ]
    }
  ]
};

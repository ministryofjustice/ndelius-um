const config = require('./.pa11yci');
// Override the default config to include verbose messages (for report output)
module.exports = {
  ...config,
  defaults: {
    ...config.defaults,
    includeWarnings: true,
    includeNotices: true
  }
};

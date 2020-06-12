
//Global Utility method to objtain a reference to a local logger.
var LoggerFactory = {};
LoggerFactory.getLogger = function(polymerClass){
  var logger = log4javascript.getLogger(polymerClass.localName);
  var consoleAppender= new log4javascript.BrowserConsoleAppender();
  var layout = new log4javascript.PatternLayout('[%c] %d{HH:mm:ss} %-5p - %m%n');
  consoleAppender.setLayout(layout);
  logger.addAppender(consoleAppender);
  return logger;
};

//function getParameterByName(name) {
//  name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
//  var regex = new RegExp('[\\?&]' + name + '=([^&#]*)'),
//    results = regex.exec(location.hash);
//  return results === null ? null : decodeURIComponent(results[1].replace(/\+/g, ' '));
//}

(function(document) {
  'use strict';

  // Grab a reference to our auto-binding template
  // and give it some initial binding values
  // Learn more about auto-binding templates at http://goo.gl/Dx1u2g
  var app = document.querySelector('#app');

  app.displayInstalledToast = function() {
    document.querySelector('#caching-complete').show();
  };

  // Listen for template bound event to know when bindings
  // have resolved and content has been stamped to the page
  app.addEventListener('dom-change', function() {
    LoggerFactory.getLogger('').info('Application Started');
  });

  // See https://github.com/Polymer/polymer/issues/1381
  window.addEventListener('WebComponentsReady', function() {
    // imports are loaded and elements have been registered
    LoggerFactory.getLogger('').debug('WebComponentsReady');
  });



})(document);

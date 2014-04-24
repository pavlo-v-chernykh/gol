var loadNpmTasks = require('load-grunt-tasks');
var timeGrunt = require('time-grunt');
var renderTask = require('./grunt/render');

module.exports = function (grunt) {
  grunt.config.init({
    copy: {
      react: {
        src: ["bower_components/react/*.js"],
        dest: "resources/public/js/react",
        flatten: true,
        expand: true
      }
    },
    watch: {
      react: {
        files: ["resources/public/js/gol/react/**/*.js"],
        tasks: ["render:react"]
      }
    },
    render: {
      react: {
        func: function () {
          React = require("react");
          require("./resources/public/js/gol/react/goog/bootstrap/nodejs");
          require("./resources/public/js/gol/react/react");
          require("./resources/public/js/gol/react/gol/react/page");
          return gol.react.page.main();
        },
        dest: "resources/public/react.html"
      }
    },
    connect: {
      server: {
        options: {
          port: 9000,
          base: "resources/public"
        }
      }
    }
  });
  loadNpmTasks(grunt);
  timeGrunt(grunt);
  grunt.registerMultiTask('render', renderTask(grunt));
  grunt.registerTask("serve", ["connect:server:keepalive"]);
  grunt.registerTask("default", ["render", "serve"]);
};

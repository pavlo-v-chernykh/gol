module.exports = function (grunt) {
  return function () {
    var done = this.async();
    var dest = this.data.dest;
    var script = "console.log((" + this.data.func.toString() + ")());";
    grunt.util.spawn({cmd: "node", args: ["-e", script]}, function (e, r, c) {
      if (!e || !c) {
        grunt.file.write(dest, r.stdout);
      } else {
        grunt.log.write(e || c);
      }
      done();
    });
  };
};

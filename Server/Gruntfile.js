module.exports = function (grunt) {

	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		yuidoc: {
			compile: {
				name: '<%= pkg.name %>',
				description: '<%= pkg.description %>',
				version: '<%= pkg.version %>',
				url: '<%= pkg.homepage %>',
				logo: '..\\..\\Android\\Cleanium\\app\\src\\main\\res\\drawable-xxhdpi\\logo.png',
				options: {
					paths: './cloud',
					outdir: 'docs',
					themedir: "node_modules/yuidoc-bootstrap-theme",
					helpers: ["node_modules/yuidoc-bootstrap-theme/helpers/helpers.js"]
				}
			}
		},
		doxx: {
			all: {
				src: './cloud',
				target: './docs',
				options: {
					title: 'Cleanium Server-side documentation',
					readme: '../README.md'
				}
			}
		},
		jsdoc: {
			dist: {
				src: ['cloud/*.js'],
				options: {
					destination: 'docs',
					footer: 'Singularity Cleanium Server',
					copyright: 'Singularity Software 2015-2016 All rights reserved',
					theme: 'amelia'
				}
			}
		},
		jshint: {
			files: ['Gruntfile.js', 'src/**/*.js', 'test/**/*.js'],
			options: {
				globals: {
					jQuery: true
				}
			}
		},
		watch: {
			files: ['<%= jshint.files %>'],
			tasks: ['jshint']
		}
	});
	//
	//	grunt.loadNpmTasks('grunt-contrib-jshint');
	//	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-doxx');
	grunt.loadNpmTasks('grunt-jsdoc');
	grunt.loadNpmTasks('grunt-contrib-yuidoc');

	//	grunt.registerTask('default', ['jshint']);
	grunt.registerTask('doc', ['doxx']);
	grunt.registerTask('yui', ['yuidoc']);

};
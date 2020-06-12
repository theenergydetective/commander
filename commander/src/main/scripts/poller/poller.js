//npm install xml2js
//npm install request
//npm install mkdirp

//This is a script to poll an ECC's stats page every 30 seconds and save it to disk.
var request = require('request');
var mkdirp = require('mkdirp');
var fs = require('fs');

//Create the directory if it doesn't exist
var dir = process.argv[2].replace('http://','').replace('/stats.xml', '');
var ipAddress = process.argv[2];

console.log('Writing files to directory ' + dir);

function pad(v){
    if (v < 10) return '0' + v;
    return v;
}

var requestStats = function(rootDir, ipAddress){
    var d = new Date();
    var fileDir = rootDir + '/' + d.getYear() + '-' + pad(d.getMonth()) + '-' + pad(d.getDate());
    var fileName = fileDir + '/' + d.getHours() + '-' + pad(d.getMinutes()) + '-' + pad(d.getSeconds());

    mkdirp(fileDir, new function(error){
        if (error){
            console.log('directory create error: ' + error);
        } else {
            request(ipAddress, function(error, response, body) {
                if (error){
                    console.log('Request Error:' + error);
                } else {
                    fs.writeFile(fileName, body, function (err) {
                        if (err) {
                            console.log('file write error' + err);
                        } else {
                            console.log(fileName + ' was saved')
                        }
                    });
                }
            });
        }
    });
};

function doWork(){
    requestStats(dir, ipAddress);
}

console.log('Starting polling of ' + process.argv[2]);
var myInterval = setInterval(doWork, 30000);






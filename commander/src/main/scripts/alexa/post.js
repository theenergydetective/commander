/**
 * Lambda script to act as a secure go-between for Alexa and TED Commander.
 * TODO: See about calling a web service directly so we can circumvent Lambda. This is required for development testing
 */


var http = require('http');

function PostCode(codestring) {
    var post_options = {
        host: 'commander.petecode.com',
        port: '8080',
        path: '/api/alexa/summary',
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
            //'Content-Length': Buffer.byteLength(post_data)
        }
    };

    // Set up the request
    var post_req = http.request(post_options, function(res) {
        res.setEncoding('utf8');
        res.on('data', function (chunk) {
            console.log('Response: ' + chunk);
            callback(FormatResponse(chunk));
        });
    });
    // post the data
    post_req.write(codestring);
    post_req.end();
}

function FormatResponse(response){
    return {
        "version": "1.0",
        "sessionAttributes": {},
        "response": {
            "outputSpeech": {
                "type": "PlainText",
                "text": response
            },
            "shouldEndSession": false
        }
    };
}

exports.handler = (event, context, callback) => {
    PostCode(event.data, callback);
};




/** Force URLs in srcset attributes into HTTPS scheme.* This is particularly useful when you're running a Flexible SSL frontend like Cloudflare*/

function ssl_srcset( $sources ) {
    foreach ( $sources as &$source )
        {
            $source['url'] = set_url_scheme( $source['url'], 'https' );
        }
        return $sources;
}

add_filter( 'wp_calculate_image_srcset', 'ssl_srcset' );


fs = require('fs');

String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};

fs.readFile('46-weather.txt', 'utf8', function (err,data) {
    if (err) {
        return console.log(err);
    }

    data = data.replaceAll('{"dt":', '\n{"dt":');

    var lines = data.split(/\n/);

    var lastDT = 0;
    for (var i=0; i < lines.length; i++){
        var line = lines[i];
        if (line.trim().length > 0){
            line = line.trim();
            var obj = JSON.parse(line);
            if (lastDT !== obj.dt) {
                var d = new Date(obj.dt * 1000);
                var month = d.getMonth();
                var date = d.getDate();
                var hour = d.getHours();
                var temp = obj.main.temp;

                //console.log(month + ',' + date + ',' + hour + ',' + temp);
                console.log(d + ' ' + temp);
                //lastDT = obj.dt;
            }
        }
    }

});
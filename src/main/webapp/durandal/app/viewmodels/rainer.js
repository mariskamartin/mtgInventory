/**
 * Created by MAR on 12.2.2015.
 */
define(['plugins/http', 'knockout'], function (http, ko) {
    var url = 'http://api.flickr.com/services/feeds/photos_public.gne';

    var qs = {
        tags: 'mount ranier',
        tagmode: 'any',
        format: 'json'
    };

    return {
        images: ko.observableArray([]),
        activate: function () {
            console.log(arguments);
            var that = this;
            if (this.images().length > 0) {
                return;
            }

            return http.jsonp(url, qs, 'jsoncallback').then(function(response) {
                that.images(response.items);
            });
        }
    };
});
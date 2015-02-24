/**
 * Created by MAR on 17.2.2015.
 */
define(function () {
    this.mtgiResizeIframe = function(obj) {
        obj.style.height = obj.contentWindow.document.body.scrollHeight + 'px';
    };

    return {};
});
jQuery(function($) {

    checkCookie_eu();

    function checkCookie_eu()
    {

        var consent = getCookie_eu("cookies_consent");

        if (consent == null || consent == "" || consent == undefined)
        {
            // show notification bar
            $('#cookie_directive_container').show();
        }

    }

    function setCookie_eu(c_name,value,exdays)
    {

        var exdate = new Date();
        exdate.setDate(exdate.getDate() + exdays);
        var c_value = escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
        document.cookie = c_name + "=" + c_value+"; path=/";

        $('#cookie_directive_container').hide('slow');
    }


    function getCookie_eu(c_name)
    {
        var i,x,y,ARRcookies=document.cookie.split(";");
        for (i=0;i<ARRcookies.length;i++)
        {
            x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
            y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
            x=x.replace(/^\s+|\s+$/g,"");
            if (x==c_name)
            {
                return unescape(y);
            }
        }
    }

    $("#cookie_accept .btn").click(function(){
        setCookie_eu("cookies_consent", 1, 30);
    });

});

function hideNav() {
    let elem = $(".navbar-image.fixed-top");
    if (elem.css("display") === "none") {
        elem.slideDown();
        document.getElementsByTagName("i")[0].className = "fa fa-sort-up";
        document.getElementsByTagName("i")[0].style.marginTop = "0px";
    } else {
        elem.slideUp();
        document.getElementsByTagName("i")[0].className = "fa fa-sort-down";
        document.getElementsByTagName("i")[0].style.marginTop = "-16px";
    }
}

function toggleDropdown(elem) {
    let panel = elem.parent(".panel");
    let panel_body = panel.children(".panel-body");
    let panel_icon = elem.children("p").children("i");

    if (panel_body.css("display") === "none") {
        panel_body.slideDown();
        panel_icon.removeClass("fa-chevron-down");
        panel_icon.addClass("fa-chevron-up");
    } else {
        panel_body.slideUp();
        panel_icon.removeClass("fa-chevron-up");
        panel_icon.addClass("fa-chevron-down");
    }
}
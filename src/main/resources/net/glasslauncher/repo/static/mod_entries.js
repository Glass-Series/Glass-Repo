$(document).on("click", ".panel div.clickable", function (e) {
    toggleDropdown($(this))
});

$(document).ready(function(e){
    let classy = '.autocollapse';

    let found = $(classy);
    found.find('.panel-body').hide();
    found.removeClass(classy);
});
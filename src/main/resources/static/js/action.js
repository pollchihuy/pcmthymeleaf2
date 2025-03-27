function changePageSize(){
    $("#itemSizeForm").submit();
}

function checkAll(checkEm,divNames) {
    var cbs = document.getElementsByTagName('input');
    for (var i = 0; i < cbs.length; i++) {
        if (cbs[i].type == 'checkbox') {
            if (cbs[i].name == divNames) {
                cbs[i].checked = checkEm;
            }
        }
    }
}
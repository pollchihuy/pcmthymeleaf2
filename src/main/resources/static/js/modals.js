function funcModalsHandler(event)
{
    event.preventDefault(); //prevent default action
    var button = event.target
    var dataTitle = button.dataset.title
    var dataTarget = button.dataset.target
    var urlz = button.dataset.url
    var serverz = button.dataset.server
    $(dataTarget).on('show.bs.modal',function(){
        $.get(urlz, function (data) {
            let pattern = /Login/i;
            let result = data.match(pattern);
            try{
                if(result){
                    window.location = "/er";
                }else{
                    $(serverz).html(data);
                }
            }catch(r)
            {
                console.log('error '+r)
            }finally
            {
                $(dataTarget).find('.modal-title').text(dataTitle)
            }
        });
    })
}

function funcModalsDataTableHandler(dataTitle,dataTarget,urlz,serverz)
{
    $(dataTarget).on('show.bs.modal',function(){

        $.get(urlz, function (data) {
            try{
                $(serverz).html(data);
            }catch(r)
            {
                console.log('error '+r)
            }finally
            {
                $(dataTarget).find('#data-title').text(dataTitle)
            }
        });
    })
}

function funcModalsDataMasterHandler(idComp,descComp,idVal,descVal)
{
    // console.log(idComp+'--'+descComp+'--'+idVal+'--'+descVal+'--')
    $(idComp).val(idVal);
    $(descComp).val(descVal);
    $('#dataTable').modal('hide');
}

$('#sizeChange').on("change", function (){
    callDataMaster();
});

function getRequestHandler(event)
{
    event.preventDefault();
    var button = event.target
    var urlz = button.dataset.url
    var dataTitle = button.dataset.title
    $.get(urlz, function (data) {
        try{
            confirm('Data Berhasil Dihapus');
            window.location = "/"+dataTitle;
        }catch(r)
        {
            console.log('error '+r)
        }
    });
}
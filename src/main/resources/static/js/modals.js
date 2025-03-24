function funcModalsHandler(event)
{
    event.preventDefault(); //prevent default action
    // event.stopImmediatePropagation();
    var button = event.target
    var dataTitle = button.dataset.title
    var dataTarget = button.dataset.target
    var urlz = button.dataset.url
    var serverz = button.dataset.server
    console.log(dataTitle+' '+dataTarget+' '+urlz+' '+serverz);
    $(dataTarget).on('show.bs.modal',function(){
        $.get(urlz, function (data) {
            let pattern = /Login/i;
            let result = data.match(pattern);
            try{
                if(result){
                    window.location = "/auth/relogin";
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
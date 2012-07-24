$(function(){
    var jsonPathVisible      = true;
    var defaultRenderVisible = false;

    function setJsonPathButtonLabel(visible) {
        $('#toggle-jsonpath').attr('value', (visible ? 'Hide' : 'Show') + ' JSONPath')
    }

    function setDefaultRenderButtonLabel(visible) {
        $('#toggle-defaultrender').attr('value', (visible ? 'Hide' : 'Show') + ' Default Render')
    }

    setJsonPathButtonLabel(jsonPathVisible);
    $('#toggle-jsonpath').click(function(){
        if(jsonPathVisible) {
            $('.jsonpath').hide();
        } else {
            $('.jsonpath').show();
        }
        jsonPathVisible = !jsonPathVisible;
        setJsonPathButtonLabel(jsonPathVisible);
    });

    setDefaultRenderButtonLabel(defaultRenderVisible);
    $('#toggle-defaultrender').click(function(){
        if(defaultRenderVisible) {
            $('.defaultrender').hide();
        } else {
            $('.defaultrender').show();
        }
        defaultRenderVisible = !defaultRenderVisible;
        setDefaultRenderButtonLabel(defaultRenderVisible);
    });

    modified = [];
    $('#type-filter,#name-filter').change(function(){
        typeFilter = $('#type-filter').val();
        nameFilter = $('#name-filter').val();
        for(var i in modified) {
            modified[i].attr('style', 'background-color: white')
        }
        modified = [];
        filter = "";
        if(typeFilter.length > 0) {
           filter = "[itemtype^='" + typeFilter + "']";
        }
        if(nameFilter.length > 0) {
            filter += "[name^='" + nameFilter + "']";
        }
        $(filter).each(function(i,v){
            $v = $(v);
            $v.attr('style', 'background-color: red');
            modified.push($v);
        });
        $('#search-report').text( modified.length + ' elements found.')
    });
});
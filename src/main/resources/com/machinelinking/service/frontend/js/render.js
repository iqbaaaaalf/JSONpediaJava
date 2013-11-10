$(function(){
    var jsonPathVisible      = false;
    var defaultRenderVisible = false;

    function setJsonPathVisible(visible) {
        $('#toggle-jsonpath').attr('value', (visible ? 'Hide' : 'Show') + ' JSONPath');
        if(visible) {
            $('.jsonpath').show();
        } else {
            $('.jsonpath').hide();
        }
    }

    function setDefaultRenderVisible(visible) {
        $('#toggle-defaultrender').attr('value', (visible ? 'Hide' : 'Show') + ' Default Render');
        if (visible) {
            $('.defaultrender').show();
        } else {
            $('.defaultrender').hide();
        }

    }

    setJsonPathVisible(jsonPathVisible);
    $('#toggle-jsonpath').click(function(){
        jsonPathVisible = !jsonPathVisible;
        setJsonPathVisible(jsonPathVisible);
    });

    setDefaultRenderVisible(defaultRenderVisible);
    $('#toggle-defaultrender').click(function(){
        defaultRenderVisible = !defaultRenderVisible;
        setDefaultRenderVisible(defaultRenderVisible);
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
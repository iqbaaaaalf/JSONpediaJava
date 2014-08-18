/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

$(function(){
    var jsonPathVisible      = false;
    var defaultRenderVisible = false;

    function setDefaultRenderVisible(visible) {
        $('#toggle-defaultrender').attr('value', (visible ? 'Hide' : 'Show') + ' Default Render');
        if (visible) {
            $('.defaultrender').show();
        } else {
            $('.defaultrender').hide();
        }

    }

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

    // jsonpath tooltip.
    $('div[title]').tooltip();
    $('span[title]').tooltip();

    // mappings accordion
    $(function () {
        $(".mapping-accordion").accordion({
            collapsible: true,
            active: false
        });
    });
});
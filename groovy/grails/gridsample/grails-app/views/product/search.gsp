  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <link rel="stylesheet" type="text/css" href="/gridsample/js/ext-2.0/resources/css/ext-all.css" />
        <g:javascript library="prototype" />
        <g:javascript src="ext-2.0/adapter/ext/ext-base.js" />
        <g:javascript src="ext-2.0/ext-all.js" />
        <title>Show Product</title>
    </head>
    <body>
        <g:remoteLink action="searchResult" onSuccess="updateList(e)">Search</g:remoteLink>

        <div id="grid-sample"></div>

        <g:javascript>
            Ext.BLANK_IMAGE_URL = "/gridsample/js/ext-2.0/resources/images/default/s.gif";

            var data = [];

            var tstore = new Ext.data.SimpleStore({
                fields: [
                    {name: 'name'},
                    {name: 'fileName'}
                ]
            });

            tstore.loadData(data);

            var grid = new Ext.grid.GridPanel({
                columns: [
                    {id: 'name', header: 'Name', sortable: true, dataIndex: 'name'},
                    {header: 'FileName', sortable: true, dataIndex: 'fileName'}
                    ],
                store: tstore,
                width: 400,
                height: 200,
                stripeRows: true,
                autoExpandColumn: 'name'
            });

            grid.render('grid-sample');
            grid.getSelectionModel().selectFirstRow();

            function updateList(e) {
                var data = eval("(" + e.responseText + ")");
                tstore.loadData(data);

                grid.render('grid-sample');
            }
        </g:javascript>

    </body>
</html>

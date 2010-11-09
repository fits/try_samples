  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <g:javascript library="prototype" />
        <title>Ajax Test</title>
    </head>
    <body>
        <g:javascript>
            function updateData(e) {
                $("data").update(e.responseText);
            }
        </g:javascript>

        <g:remoteLink action="find" onSuccess="updateData(e)">Find Data</g:remoteLink>
        <hr />
        <div id="data" />

    </body>
</html>

<?php

use Illuminate\Http\Request;

use App\Http\Controllers\ItemController;

function service($type, $base)
{
    $app = app();

    $name = ucfirst($type) . $base;

    if ($app->has($name)) {
        return $app->make($name);
    }

    return $app->make($base);
}

Route::prefix('svc-{type}')->group(function() {

    Route::get('item/find/{kw}', function (ItemController $ctl, $type, $kw) {
        return $ctl->find(service($type, 'ItemService'), $kw);
    });
});

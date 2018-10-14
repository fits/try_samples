<?php

namespace App\Http\Controllers;

use App\Item\ItemService;

class ItemController extends Controller
{
    public function find(ItemService $service, $kw)
    {
        return $service->find($kw);
    }
}

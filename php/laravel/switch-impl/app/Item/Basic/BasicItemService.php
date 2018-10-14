<?php

namespace App\Item\Basic;

use App\Item\ItemService;

class BasicItemService implements ItemService
{
    public function find($kw)
    {
        return "BASIC-ITEM:" . $kw;
    }
}

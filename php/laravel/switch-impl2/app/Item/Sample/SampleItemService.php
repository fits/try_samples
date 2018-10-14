<?php

namespace App\Item\Sample;

use App\Item\ItemService;

class SampleItemService implements ItemService
{
    public function find($kw)
    {
        return "sample-item:" . $kw;
    }
}

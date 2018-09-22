<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Product;

class ProductController extends Controller
{
    //
    public function create(Request $request)
    {
        $data = $request->validate([
            'name' => 'required',
            'price' => 'required|integer',
        ]);

        $product = Product::create($data);

        return ['id' => $product->id];
    }

    public function find($id)
    {
        return Product::find($id);
    }
}

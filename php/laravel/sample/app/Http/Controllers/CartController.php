<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;

use App\Cart;

class CartController extends Controller
{
    public function create()
    {
        $cart = new Cart;
        $cart->save();

        return ['id' => $cart->id];
    }

    public function delete($id)
    {
        Cart::findOrFail($id)->delete();
    }

    public function find($id)
    {
        return Cart::with(['items', 'items.product'])->findOrFail($id);
    }

    public function addItem(Request $request, $id)
    {
        $data = $request->validate([
            'product_id' => 'required|exists:products,id',
            'qty' => 'required|integer|between:1,9',
        ]);

        $item = Cart::findOrFail($id)->items()->create($data);

        return ['item_id' => $item->id];
    }

    public function removeItem($id, $itemId)
    {
        Cart::findOrFail($id)->items()->findOrFail($itemId)->delete();
    }
}

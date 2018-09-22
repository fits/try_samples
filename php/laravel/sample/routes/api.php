<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::post('/products', 'ProductController@create');
Route::get('/products/{id}', 'ProductController@find');

Route::post('/carts', 'CartController@create');
Route::delete('/carts/{id}', 'CartController@delete');
Route::get('/carts/{id}', 'CartController@find');

Route::post('/carts/{id}', 'CartController@addItem');
Route::delete('/carts/{id}/{itemId}', 'CartController@removeItem');

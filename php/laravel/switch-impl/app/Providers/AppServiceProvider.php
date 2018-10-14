<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Bootstrap any application services.
     *
     * @return void
     */
    public function boot()
    {
        //
    }

    /**
     * Register any application services.
     *
     * @return void
     */
    public function register()
    {
        $this->app->bind('BasicItemService', 'App\Item\Basic\BasicItemService');
        $this->app->bind('SampleItemService', 'App\Item\Sample\SampleItemService');

        $this->app->alias('BasicItemService', 'ItemService');
    }
}

# -*- coding: utf-8 -*-
from odoo import http
from odoo.http import request

class Sample1(http.Controller):
    @http.route('/sample/', auth='public')
    def home(self, **kw):
        return "sample data"

    @http.route('/sample/<int:item_id>/name', auth='public')
    def name(self, item_id, **kw):
        return request.env['sample.item'].browse(item_id).name

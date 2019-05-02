# -*- coding: utf-8 -*-

from odoo import models, fields, api

class SampleItem(models.Model):
    _name = 'sample.item'

    name = fields.Char(required = True)
    value = fields.Integer()
    compute_value = fields.Integer(compute = '_compute_value')

    @api.depends('value')
    def _compute_value(self):
        for r in self:
            r.compute_value = r.value * 10 + 5

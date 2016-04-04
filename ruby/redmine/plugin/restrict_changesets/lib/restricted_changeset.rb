module RestrictedChangeset

  def self.included(base)
    base.singleton_class.prepend StaticMethods
  end

  module StaticMethods
    def visible
      q = super.limit(5).reverse_order

      q.define_singleton_method(:to_a) { super().reverse }

      q
    end
  end

end

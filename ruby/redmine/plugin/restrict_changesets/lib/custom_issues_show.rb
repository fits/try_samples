module CustomIssuesShow

  def self.included(base)
    base.prepend ShowMethod
  end

  module ShowMethod
    def show
      if request.format.html?

        @issue.changesets.define_singleton_method(:visible) {
          q = super().limit(5).reverse_order

          q.define_singleton_method(:to_a) { super().reverse }

          q
        }

        # 以下でも可
        #class << @issue.changesets
        #  def visible
        #    q = super().limit(5).reverse_order
        #
        #    def q.to_a
        #      super.reverse
        #    end
        #
        #    q
        #  end
        #end
      end

      super
    end
  end
end

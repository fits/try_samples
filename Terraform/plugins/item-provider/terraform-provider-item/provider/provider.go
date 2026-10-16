package provider

import (
	"context"

	"github.com/hashicorp/terraform-plugin-framework/datasource"
	"github.com/hashicorp/terraform-plugin-framework/provider"
	"github.com/hashicorp/terraform-plugin-framework/provider/schema"
	"github.com/hashicorp/terraform-plugin-framework/resource"
	"github.com/hashicorp/terraform-plugin-framework/types"
)

const DEFAULT_ENDPOINT = "http://127.0.0.1:8000/items"

var _ provider.Provider = &itemProvider{}

type itemProvider struct{}

type itemProviderModel struct {
	Endpoint types.String `tfsdk:"endpoint"`
}

func New() func() provider.Provider {
	return func() provider.Provider {
		return &itemProvider{}
	}
}

func (p *itemProvider) Configure(ctx context.Context, req provider.ConfigureRequest, resp *provider.ConfigureResponse) {
	var data itemProviderModel
	resp.Diagnostics.Append(req.Config.Get(ctx, &data)...)

	if resp.Diagnostics.HasError() {
		return
	}

	endpoint := DEFAULT_ENDPOINT

	if !data.Endpoint.IsNull() {
		endpoint = data.Endpoint.ValueString()
	}

	client := ItemClient{endpoint}

	resp.ResourceData = client
}

func (p *itemProvider) Metadata(ctx context.Context, req provider.MetadataRequest, resp *provider.MetadataResponse) {
	resp.TypeName = "item"
}

func (p *itemProvider) DataSources(ctx context.Context) []func() datasource.DataSource {
	return []func() datasource.DataSource{}
}

func (p *itemProvider) Resources(ctx context.Context) []func() resource.Resource {
	return []func() resource.Resource{
		NewItemResource,
	}
}

func (p *itemProvider) Schema(ctx context.Context, req provider.SchemaRequest, resp *provider.SchemaResponse) {
	resp.Schema = schema.Schema{
		Attributes: map[string]schema.Attribute{
			"endpoint": schema.StringAttribute{
				MarkdownDescription: "item-server items endpoint",
				Optional:            true,
			},
		},
	}
}

package provider

import (
	"context"
	"time"

	"github.com/hashicorp/terraform-plugin-framework/datasource"
	"github.com/hashicorp/terraform-plugin-framework/datasource/schema"
	"github.com/hashicorp/terraform-plugin-framework/types"
	"github.com/hashicorp/terraform-plugin-log/tflog"
)

var _ datasource.DataSource = &customDataSource{}

type customDataSource struct{}

type customData struct {
	Id types.String `tfsdk:"id"`
	At types.Int64  `tfsdk:"at"`
}

func NewDataSource() datasource.DataSource {
	return &customDataSource{}
}

func (d *customDataSource) Metadata(ctx context.Context, req datasource.MetadataRequest, resp *datasource.MetadataResponse) {
	resp.TypeName = req.ProviderTypeName + "_custom"
}

func (d *customDataSource) Schema(ctx context.Context, req datasource.SchemaRequest, resp *datasource.SchemaResponse) {
	resp.Schema = schema.Schema{
		Attributes: map[string]schema.Attribute{
			"id": schema.StringAttribute{
				Required: true,
			},
			"at": schema.Int64Attribute{
				MarkdownDescription: "datetime now (ms)",
				Optional:            true,
			},
		},
	}
}

func (d *customDataSource) Read(ctx context.Context, req datasource.ReadRequest, resp *datasource.ReadResponse) {
	tflog.Info(ctx, "read customDataSource")

	var data customData

	resp.Diagnostics.Append(req.Config.Get(ctx, &data)...)

	if resp.Diagnostics.HasError() {
		return
	}

	var now = time.Now().UnixMilli()

	data.At = types.Int64Value(now)

	resp.Diagnostics.Append(resp.State.Set(ctx, &data)...)
}

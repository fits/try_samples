package provider

import (
	"context"

	"github.com/hashicorp/terraform-plugin-framework/path"
	"github.com/hashicorp/terraform-plugin-framework/resource"
	"github.com/hashicorp/terraform-plugin-framework/resource/schema"
	"github.com/hashicorp/terraform-plugin-framework/types"
	"github.com/hashicorp/terraform-plugin-log/tflog"
)

var _ resource.Resource = &itemResource{}

type itemResource struct {
	Client ItemClient
}

type itemResourceModel struct {
	Id    types.String `tfsdk:"id"`
	Value types.Int32  `tfsdk:"value"`
}

func NewItemResource() resource.Resource {
	return &itemResource{}
}

func (r *itemResource) Metadata(ctx context.Context, req resource.MetadataRequest, resp *resource.MetadataResponse) {
	resp.TypeName = req.ProviderTypeName + "_data"
}

func (r *itemResource) Schema(ctx context.Context, req resource.SchemaRequest, resp *resource.SchemaResponse) {
	resp.Schema = schema.Schema{
		Attributes: map[string]schema.Attribute{
			"id": schema.StringAttribute{
				Computed: true,
			},
			"value": schema.Int32Attribute{
				Optional: true,
			},
		},
	}
}

func (r *itemResource) Configure(ctx context.Context, req resource.ConfigureRequest, resp *resource.ConfigureResponse) {
	if req.ProviderData == nil {
		return
	}

	client, ok := req.ProviderData.(ItemClient)

	if !ok {
		resp.Diagnostics.AddError("Unexpected ProviderData", "Expected ItemClient")
		return
	}

	r.Client = client
}

func (r *itemResource) Create(ctx context.Context, req resource.CreateRequest, resp *resource.CreateResponse) {
	var data itemResourceModel
	resp.Diagnostics.Append(req.Plan.Get(ctx, &data)...)

	if resp.Diagnostics.HasError() {
		return
	}

	item, err := r.Client.Create(data.Value.ValueInt32())

	if err != nil {
		resp.Diagnostics.AddError("failed create", err.Error())
		return
	}

	tflog.Trace(ctx, "created item resource")

	data.Id = types.StringValue(item.Id)
	data.Value = types.Int32Value(int32(item.Value))

	resp.Diagnostics.Append(resp.State.Set(ctx, data)...)
}

func (r *itemResource) Update(ctx context.Context, req resource.UpdateRequest, resp *resource.UpdateResponse) {
	var data itemResourceModel
	resp.Diagnostics.Append(req.Plan.Get(ctx, &data)...)

	if resp.Diagnostics.HasError() {
		return
	}

	var id types.String
	resp.Diagnostics.Append(req.State.GetAttribute(ctx, path.Root("id"), &id)...)

	if resp.Diagnostics.HasError() {
		return
	}

	item, err := r.Client.Update(id.ValueString(), data.Value.ValueInt32())

	if err != nil {
		resp.Diagnostics.AddError("failed update", err.Error())
		return
	}

	tflog.Trace(ctx, "updated item resource")

	data.Id = types.StringValue(item.Id)
	data.Value = types.Int32Value(int32(item.Value))

	resp.Diagnostics.Append(resp.State.Set(ctx, data)...)
}

func (r *itemResource) Read(ctx context.Context, req resource.ReadRequest, resp *resource.ReadResponse) {
	var data itemResourceModel
	resp.Diagnostics.Append(req.State.Get(ctx, &data)...)

	if resp.Diagnostics.HasError() {
		return
	}

	item, err := r.Client.Find(data.Id.ValueString())

	if err != nil {
		resp.Diagnostics.AddError("failed read", err.Error())
		return
	}

	tflog.Trace(ctx, "found item resource")

	data.Id = types.StringValue(item.Id)
	data.Value = types.Int32Value(int32(item.Value))

	resp.Diagnostics.Append(resp.State.Set(ctx, data)...)
}

func (r *itemResource) Delete(ctx context.Context, req resource.DeleteRequest, resp *resource.DeleteResponse) {
	var data itemResourceModel
	resp.Diagnostics.Append(req.State.Get(ctx, &data)...)

	if resp.Diagnostics.HasError() {
		return
	}

	err := r.Client.Delete(data.Id.ValueString())

	if err != nil {
		resp.Diagnostics.AddError("failed delete", err.Error())
		return
	}

	tflog.Trace(ctx, "deleted item resource")
}

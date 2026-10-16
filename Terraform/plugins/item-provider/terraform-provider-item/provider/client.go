package provider

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"strings"
)

const CONTENT_TYPE = "application/json"

type ItemClient struct {
	Endpoint string
}

type Item struct {
	Id    string `json:"id"`
	Value int32  `json:"value"`
}

type ItemInput struct {
	Value int32 `json:"value"`
}

func (c *ItemClient) Create(value int32) (*Item, error) {
	input := ItemInput{value}

	reader, err := toReader(input)

	if err != nil {
		return nil, err
	}

	resp, err := http.Post(c.Endpoint, CONTENT_TYPE, reader)

	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if isOk(resp.StatusCode) {
		return toItem(resp.Body)
	} else {
		return nil, errors.New("failed create")
	}
}

func (c *ItemClient) Find(id string) (*Item, error) {
	resp, err := http.Get(c.itemEndpoint(id))

	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if isOk(resp.StatusCode) {
		return toItem(resp.Body)
	} else {
		return nil, fmt.Errorf("failed find: statusCode=%d, id=%s", resp.StatusCode, id)
	}
}

func (c *ItemClient) Update(id string, value int32) (*Item, error) {
	input := ItemInput{value}

	reader, err := toReader(input)

	if err != nil {
		return nil, err
	}

	req, err := http.NewRequest(http.MethodPut, c.itemEndpoint(id), reader)

	if err != nil {
		return nil, err
	}

	resp, err := http.DefaultClient.Do(req)

	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if isOk(resp.StatusCode) {
		return toItem(resp.Body)
	} else {
		return nil, fmt.Errorf("failed update: statusCode=%d, id=%s, value=%d", resp.StatusCode, id, value)
	}
}

func (c *ItemClient) Delete(id string) error {
	req, err := http.NewRequest(http.MethodDelete, c.itemEndpoint(id), nil)

	if err != nil {
		return err
	}

	resp, err := http.DefaultClient.Do(req)

	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if isOk(resp.StatusCode) {
		return nil
	} else {
		return fmt.Errorf("failed delete: statusCode=%d, id=%s", resp.StatusCode, id)
	}
}

func (c *ItemClient) itemEndpoint(id string) string {
	if strings.HasSuffix(c.Endpoint, "/") {
		return c.Endpoint + id
	} else {
		return c.Endpoint + "/" + id
	}
}

func isOk(statusCode int) bool {
	return statusCode >= 200 && statusCode < 300
}

func toItem(r io.Reader) (*Item, error) {
	var item Item
	err := json.NewDecoder(r).Decode(&item)

	return &item, err
}

func toReader(v any) (io.Reader, error) {
	buf, err := json.Marshal(v)

	if err != nil {
		return nil, err
	}

	return bytes.NewReader(buf), nil
}

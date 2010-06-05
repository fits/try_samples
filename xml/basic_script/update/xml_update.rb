require "rexml/document"

xml = REXML::Document.new File.new(ARGV[0])
doc = xml.root

#要素を追加
doc.add_element("data", {"id" => "3"}).add_element("details").add_text("added")

n = doc.get_elements("//data[@id='2']")[0]
n.add_attribute("type", "node")

#要素を削除
doc.elements["//data[@id='1']"].remove
#以下でも可
#doc.get_elements("//data[@id='1']")[0].remove
#doc.elements["data"].remove
#doc.delete_element("data")

#要素を置換
n.replace_child(n.elements[1], REXML::Element.new("text").add_text("update test"))

//要素の値を変更
n.elements[2].text = "after"

#属性の変更
n.attributes["ext"] = "updated"

puts doc

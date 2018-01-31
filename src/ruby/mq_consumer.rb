require 'bunny'
require 'faker'

$:.push('thrift-classes')

require 'first_constants'

$conn = Bunny.new("amqp://guest:guest@localhost:5672")
$conn.start
$ch = $conn.channel
$queue = $ch.queue("abcdefghi", auto_delete: true)
$deserializer = Thrift::Deserializer.new

$queue.subscribe(block: true) do |delivery_info, properties, payload|
  obj = IntAndString.new
  $deserializer.deserialize(obj, payload)
  puts "#{obj.inspect}"
end
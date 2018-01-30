require 'bunny'
require 'faker'

$:.push('thrift-classes')

require 'first_constants'

$conn = Bunny.new("amqp://guest:guest@localhost:5672")
$conn.start
$ch = $conn.channel
$queue = $ch.queue("abcdefghi", auto_delete: true)
$serializer = Thrift::Serializer.new

1000.times do |i|
  struct = IntAndString.new(name: Faker::Name.name, count: i)
  $queue.publish($serializer.serialize(struct))
end
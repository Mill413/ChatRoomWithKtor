package top.harumill.top.harumill.message

class MessageChain(message: MessageChain?=null) : Message,List<Message>{

    override val type: MessageType = MessageType.MESSAGECHAIN

    private val messageList = mutableListOf<Message>()

    override val size = messageList.size

    init {
        if (message != null) {
            messageList.addAll(message.messageList)
        }
    }

    constructor(singleMessage:Message) : this() {
        messageList.add(singleMessage)
    }

    override fun contentToString(): String {
        var content = ""
        this.forEach{
            content += it.contentToString()
        }
        return content
    }

    override fun toString(): String {
        var content = ""
        this.forEach{
            content += it.toString()
        }
        return content
    }
    operator fun plus(message:MessageChain):MessageChain{
        messageList.addAll(message.messageList)
        return this
    }

    fun add(message: Message): MessageChain {
        messageList.add(message)
        return this
    }

    override fun contains(element: Message): Boolean {
        return messageList.contains(element)
    }

    override fun containsAll(elements: Collection<Message>): Boolean {
        return messageList.containsAll(elements)
    }

    override fun get(index: Int): Message {
        return messageList[index]
    }

    override fun indexOf(element: Message): Int {
        return messageList.indexOf(element)
    }

    override fun isEmpty(): Boolean {
        return messageList.isEmpty()
    }

    override fun iterator(): Iterator<Message> {
        return messageList.iterator()
    }

    override fun lastIndexOf(element: Message): Int {
        return messageList.lastIndexOf(element)
    }

    override fun listIterator(): ListIterator<Message> {
        return messageList.listIterator()
    }

    override fun listIterator(index: Int): ListIterator<Message> {
        return messageList.listIterator()
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<Message> {
        return messageList.subList(fromIndex,toIndex)
    }


}
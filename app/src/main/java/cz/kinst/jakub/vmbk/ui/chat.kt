package cz.kinst.jakub.vmbk.ui

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import cz.kinst.jakub.vmbk.*
import cz.kinst.jakub.vmbk.databinding.FragmentChatBinding
import cz.kinst.jakub.vmbk.model.ChatMessage
import io.rapid.Rapid
import io.rapid.lifecycle.RapidLiveData
import me.tatarka.bindingcollectionadapter2.ItemBinding

interface ChatView {
    val itemBinding: ItemBinding<ChatMessage>
}


class ChatFragment : Fragment(), ChatView {
    val vmb = vmb<ChatViewModel, FragmentChatBinding>(R.layout.fragment_chat)

    override val itemBinding = ItemBinding.of<ChatMessage>(BR.message, R.layout.item_list)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = vmb.rootView
}


class ChatViewModel : ViewModel(), LifecycleReceiver {
    private val RAPID_COLLECTION = Rapid.getInstance().collection("vmbinding_test_chat_1", ChatMessage::class.java)

    val messageText = ObservableField<String>()

    val chatLiveData = RapidLiveData.from(RAPID_COLLECTION.map { it.body })!!
    val items = ObservableField<List<ChatMessage>>()

    override fun onLifecycleReady(lifecycleOwner: LifecycleOwner) {
        chatLiveData.observe(lifecycleOwner, items)
    }

    fun add() {
        RAPID_COLLECTION.newDocument().mutate(ChatMessage(messageText.get()))
                .onSuccess { messageText.set("") }
    }
}
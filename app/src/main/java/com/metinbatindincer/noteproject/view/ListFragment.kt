package com.metinbatindincer.noteproject.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.metinbatindincer.noteproject.adapter.NoteAdapter
import com.metinbatindincer.noteproject.databinding.FragmentListBinding
import com.metinbatindincer.noteproject.model.Note
import com.metinbatindincer.noteproject.roomdb.NoteDao
import com.metinbatindincer.noteproject.roomdb.NoteDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: NoteDatabase
    private lateinit var noteDao: NoteDao
    private val mDisposable= CompositeDisposable()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db= Room.databaseBuilder(requireContext(),NoteDatabase::class.java,"Notes").build()
        noteDao=db.noteDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { yeniEklendi(it) }
        binding.listRcView.layoutManager= LinearLayoutManager(requireContext())
        getData()
    }

    fun yeniEklendi(view: View){
        val action=ListFragmentDirections.actionListFragmentToNoteFragment("new",0)
        Navigation.findNavController(view).navigate(action)
    }

    fun getData() {
        mDisposable.add(
            noteDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(notes: List<Note>) {
        val adapter = NoteAdapter(notes)
        binding.listRcView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}
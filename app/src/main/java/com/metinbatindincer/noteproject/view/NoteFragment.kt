package com.metinbatindincer.noteproject.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.room.Room
import com.metinbatindincer.noteproject.databinding.FragmentNoteBinding
import com.metinbatindincer.noteproject.model.Note
import com.metinbatindincer.noteproject.roomdb.NoteDao
import com.metinbatindincer.noteproject.roomdb.NoteDatabase

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers




class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: NoteDatabase
    private lateinit var noteDao: NoteDao
    private var mDisposable= CompositeDisposable()
    private var selectNote:Note?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db=Room.databaseBuilder(requireContext(),NoteDatabase::class.java,"Notes").build()
        noteDao=db.noteDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.deleteButton.setOnClickListener { delete(it) }
        binding.backButton.setOnClickListener{
            val action=NoteFragmentDirections.actionNoteFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)
        }

        arguments?.let {
            val info = NoteFragmentArgs.fromBundle(it).info


            if(info.equals("new")){
                //new note
                binding.saveButton.setOnClickListener { save(it) }
                binding.deleteButton.visibility=View.INVISIBLE
            }else{

                binding.saveButton.setOnClickListener { update(it) }
                //old note
                val id=NoteFragmentArgs.fromBundle(it).id

                mDisposable.add(
                    noteDao.findNoteById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handerResponse)
                )

            }
        }

    }

    //new fun
    private fun update(view: View) {
        val title = binding.titleEditText.text.toString()
        val content = binding.contentEditText.text.toString()

        // Notun id'sini almak için args kullan
        val id = NoteFragmentArgs.fromBundle(requireArguments()).id

        // Güncellenmiş not oluştur ve id'yi ayarla
        val updatedNote = Note(title, content).apply {
            this.id = id
        }

        mDisposable.add(
            noteDao.update(updatedNote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForUpdate)
        )
    }


    private fun handleResponseForUpdate() {
        // Güncelleme işlemi başarılı olduğunda önceki fragmente dön
        val action = NoteFragmentDirections.actionNoteFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun handerResponse(note: Note){
       binding.titleEditText.setText(note.title)
        binding.contentEditText.setText(note.content)
        selectNote=note
    }

    fun save(view: View){
        val title=binding.titleEditText.text.toString()
        val content=binding.contentEditText.text.toString()



        val note = Note(title,content)
        mDisposable.add(noteDao.insert(note)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponseForInsert))

    }
    private fun handleResponseForInsert(){
    //önceki fragmenta dön
        val action = NoteFragmentDirections.actionNoteFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    fun delete(view: View){

        if(selectNote!=null){
            mDisposable.add(
                noteDao.delete(note = selectNote!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseForInsert)
            )
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }


}
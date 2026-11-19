package com.example.myapp.todo;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TodoController {

	@Autowired
	private TodoService todoService;
	
	//全件表示
	@GetMapping("/todoList")
	public String todoList01(Model m) {
	    // データベースから全件取得してモデルに追加
	    m.addAttribute("todos", todoService.findAll());
	    m.addAttribute("today", LocalDate.now());
		return "todo/todoList";
	}
	
	//登録
	@PostMapping("/insert")
    public String insertTodo(
            @RequestParam("TASK") String task,
            @RequestParam("DATE") LocalDate date
            ) {		
		Todo t = new Todo();
		t.setId(null);
		t.setTask(task);
		t.setDate(date);
		t.setCompleted(false);
		
		// データベースに登録する処理を追加
        todoService.insert(t);
        
        return "redirect:/todoList";
    }
	
	//削除
	@PostMapping("/delete")
	public String deleteTodo(@RequestParam("id") Long id) {
		// 1. IDを元に、今ある find(id) メソッドを使ってエンティティを取得する
		Todo t = todoService.find(id);
		// 2. データが存在すれば、今ある delete(entity) メソッドに渡して削除する
		if(t != null) {
			todoService.delete(t);
		}
		return "redirect:/todoList";
	}
	
	//変更（タスク名）
	@PostMapping("/update_task")
    public String updateTask(
    		@RequestParam(name = "ID", required = false) Long id,
            @RequestParam(name = "TASK", required = false) String task,
            RedirectAttributes errorMsg
            ) {
		//未選択時にエラー表示
		if(id == null) {
			errorMsg.addFlashAttribute("errorMsg", "変更するタスクが選択されていません。");
			return "redirect:/todoList";
		}
		if(task == null || task.isBlank()) {
			errorMsg.addFlashAttribute("errorMsg", "変更するタスク名が入力されていません。");
			return "redirect:/todoList";
		}
		
		//選択されたIDをもとに、データベースからTodoエンティティを取得する
		Todo t = todoService.find(id);
		//取得したエンティティのタスクを新しく書き換える
		t.setTask(task);
		//データベースを更新する
		todoService.update(t);
        
        return "redirect:/todoList";
	}
	
	//変更（期限日）
	@PostMapping("/update_date")
    public String updateDate(
    		@RequestParam(name = "ID", required = false) Long id,
            @RequestParam(name = "DATE", required = false) LocalDate date,
            RedirectAttributes errorMsg
            ) {
		//未選択時にエラー表示
		if(id == null) {
			errorMsg.addFlashAttribute("errorMsg", "変更するタスクが選択されていません。");
			return "redirect:/todoList";
		}
		if(date == null) {
			errorMsg.addFlashAttribute("errorMsg", "変更するタスクの期限日が入力されていません。");
			return "redirect:/todoList";
		}
		
		//選択されたIDをもとに、データベースからTodoエンティティを取得する
		Todo t = todoService.find(id);
		//取得したエンティティのタスクを新しく書き換える
		t.setDate(date);
		//データベースを更新する
		todoService.update(t);
        
        return "redirect:/todoList";
	}
	
	//チェック（完了か未完了か）
	@PostMapping("/update_check/{id}")
	public String updateCheck(
			@PathVariable("id") Long id
			){
		Todo t = todoService.find(id);
		// データが存在する場合のみ処理を実行する
		if(t != null) {
			if (t.getCompleted() == false) {
				t.setCompleted(true);
			}else {
				t.setCompleted(false);
			}
			todoService.update(t);
		}
		return "redirect:/todoList";
	}
}
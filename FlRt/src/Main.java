import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            if (args != null && args.length > 0 && "test".equalsIgnoreCase(args[0])) {
                runTestRunner(scanner);
            } else {
                runInteractiveApp(scanner);
            }
        } finally {
            scanner.close();
        }
    }

    // =================== 互動式系統：登入 / 註冊 + 專案與任務操作 ===================

    private static void runInteractiveApp(Scanner scanner) {
        System.out.println("==== Project Management System ====");
        System.out.println("此模式提供使用者註冊 / 登入與基本的專案、任務操作。");

        AuthService authService = new AuthService();
        List<Project> projects = new ArrayList<>();
        User currentUser = null;

        while (true) {
            if (currentUser == null) {
                System.out.println();
                System.out.println("== 未登入狀態 ==");
                System.out.println("1) 註冊");
                System.out.println("2) 登入");
                System.out.println("3) 離開系統");
                System.out.print("請選擇: ");
                String choice = scanner.nextLine().trim();
                if ("1".equals(choice)) {
                    System.out.print("輸入帳號(username): ");
                    String username = scanner.nextLine();
                    System.out.print("輸入密碼(password): ");
                    String password = scanner.nextLine();
                    try {
                        User user = authService.register(username, password);
                        System.out.println("註冊成功，請重新登入。 (username=" + user.getUsername() + ")");
                    } catch (Exception e) {
                        System.out.println("註冊失敗: " + e.getMessage());
                    }
                } else if ("2".equals(choice)) {
                    System.out.print("輸入帳號(username): ");
                    String username = scanner.nextLine();
                    System.out.print("輸入密碼(password): ");
                    String password = scanner.nextLine();
                    User user = authService.login(username, password);
                    if (user == null) {
                        System.out.println("登入失敗：帳號或密碼錯誤。");
                    } else {
                        currentUser = user;
                        System.out.println("登入成功，歡迎 " + currentUser.getUsername() + " !");
                    }
                } else if ("3".equals(choice)) {
                    System.out.println("離開系統，再見。");
                    return;
                } else {
                    System.out.println("無效選項，請重新輸入。");
                }
            } else {
                System.out.println();
                System.out.println("== 使用者：" + currentUser.getUsername() + " ==");
                System.out.println("1) 建立專案");
                System.out.println("2) 列出專案");
                System.out.println("3) 進入專案");
                System.out.println("4) 登出");
                System.out.print("請選擇: ");
                String choice = scanner.nextLine().trim();
                if ("1".equals(choice)) {
                    System.out.print("輸入專案名稱: ");
                    String name = scanner.nextLine();
                    Project p = new Project(name);
                    projects.add(p);
                    System.out.println("已建立專案: " + p.getName());
                } else if ("2".equals(choice)) {
                    if (projects.isEmpty()) {
                        System.out.println("目前沒有任何專案。");
                    } else {
                        System.out.println("專案清單:");
                        for (int i = 0; i < projects.size(); i++) {
                            System.out.println((i + 1) + ") " + projects.get(i).getName());
                        }
                    }
                } else if ("3".equals(choice)) {
                    if (projects.isEmpty()) {
                        System.out.println("目前沒有任何專案，請先建立一個。");
                        continue;
                    }
                    System.out.println("選擇要進入的專案編號:");
                    for (int i = 0; i < projects.size(); i++) {
                        System.out.println((i + 1) + ") " + projects.get(i).getName());
                    }
                    System.out.print("輸入編號: ");
                    String idxStr = scanner.nextLine();
                    try {
                        int idx = Integer.parseInt(idxStr) - 1;
                        if (idx < 0 || idx >= projects.size()) {
                            System.out.println("無效編號。");
                        } else {
                            enterProject(scanner, projects.get(idx), currentUser);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("請輸入數字編號。");
                    }
                } else if ("4".equals(choice)) {
                    currentUser = null;
                    System.out.println("已登出。");
                } else {
                    System.out.println("無效選項，請重新輸入。");
                }
            }
        }
    }

    private static void enterProject(Scanner scanner, Project project, User currentUser) {
        System.out.println("== 進入專案：" + project.getName() + " ==");
        Board board = new Board();
        Sprint sprint = null;

        while (true) {
            // 每次顯示選單前重新取得任務清單，確保新建立的任務會立即出現在後續查詢中
            List<Task> tasks = project.getTasks();

            System.out.println();
            System.out.println("1) 建立 Story 任務");
            System.out.println("2) 建立 SubTask 任務 (需選擇 Story)");
            System.out.println("3) 建立 Bug 任務");
            System.out.println("4) 建立 Epic 任務");
            System.out.println("5) Epic 加入 Story");
            System.out.println("6) 列出任務");
            System.out.println("7) 變更任務狀態");
            System.out.println("8) 指派 / 取消指派任務");
            System.out.println("9) 管理成員");
            System.out.println("10) 看板操作 (Board)");
            System.out.println("11) 衝刺操作 (Sprint)");
            System.out.println("12) 建立 Comment / WorkLog 範例");
            System.out.println("13) 註冊觀察者並測試通知");
            System.out.println("14) 返回上一層");
            System.out.print("請選擇: ");
            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                System.out.print("輸入 Story ID: ");
                String id = scanner.nextLine();
                System.out.print("輸入 Story Title: ");
                String title = scanner.nextLine();
                System.out.print("輸入 Story Description: ");
                String desc = scanner.nextLine();
                try {
                    Story story = new Story(id, title, desc);
                    project.addTask(story);
                    System.out.println("已建立 Story 任務: " + story.getId());
                } catch (Exception e) {
                    System.out.println("建立失敗: " + e.getMessage());
                }
            } else if ("2".equals(choice)) {
                if (tasks.isEmpty()) {
                    System.out.println("請先建立 Story 以供 SubTask 隸屬。");
                    continue;
                }
                Task storyTask = pickTaskByType(scanner, tasks, Story.class);
                if (!(storyTask instanceof Story)) {
                    System.out.println("必須選擇 Story。");
                    continue;
                }
                Story parent = (Story) storyTask;
                System.out.print("輸入 SubTask ID: ");
                String id = scanner.nextLine();
                System.out.print("輸入 SubTask Title: ");
                String title = scanner.nextLine();
                System.out.print("輸入 SubTask Description: ");
                String desc = scanner.nextLine();
                try {
                    SubTask sub = new SubTask(id, title, desc, parent);
                    parent.addSubTask(sub);
                    project.addTask(sub);
                    System.out.println("已建立 SubTask: " + sub.getId() + " 隸屬 Story " + parent.getId());
                } catch (Exception e) {
                    System.out.println("建立失敗: " + e.getMessage());
                }
            } else if ("3".equals(choice)) {
                System.out.print("輸入 Bug ID: ");
                String id = scanner.nextLine();
                System.out.print("輸入 Bug Title: ");
                String title = scanner.nextLine();
                System.out.print("輸入 Bug Description: ");
                String desc = scanner.nextLine();
                System.out.println("選擇嚴重程度: 1) LOW  2) MEDIUM  3) HIGH  4) CRITICAL");
                String se = scanner.nextLine().trim();
                Priority p = Priority.MEDIUM;
                if ("1".equals(se)) p = Priority.LOW;
                else if ("2".equals(se)) p = Priority.MEDIUM;
                else if ("3".equals(se)) p = Priority.HIGH;
                else if ("4".equals(se)) p = Priority.CRITICAL;
                try {
                    Bug bug = new Bug(id, title, desc, p);
                    project.addTask(bug);
                    System.out.println("已建立 Bug 任務: " + bug.getId() + "，severity=" + bug.getSeverity());
                } catch (Exception e) {
                    System.out.println("建立失敗: " + e.getMessage());
                }
            } else if ("4".equals(choice)) {
                System.out.print("輸入 Epic ID: ");
                String id = scanner.nextLine();
                System.out.print("輸入 Epic Title: ");
                String title = scanner.nextLine();
                System.out.print("輸入 Epic Description: ");
                String desc = scanner.nextLine();
                try {
                    Epic epic = new Epic(id, title, desc);
                    project.addTask(epic);
                    System.out.println("已建立 Epic 任務: " + epic.getId());
                } catch (Exception e) {
                    System.out.println("建立失敗: " + e.getMessage());
                }
            } else if ("5".equals(choice)) {
                Task epicTask = pickTaskByType(scanner, tasks, Epic.class);
                Task storyTask = pickTaskByType(scanner, tasks, Story.class);
                if (!(epicTask instanceof Epic) || !(storyTask instanceof Story)) {
                    System.out.println("需選 Epic 與 Story。");
                    continue;
                }
                Epic epic = (Epic) epicTask;
                Story story = (Story) storyTask;
                try {
                    epic.addStory(story);
                    System.out.println("已將 Story " + story.getId() + " 加入 Epic " + epic.getId());
                    System.out.println("Epic 完成率：" + epic.getCompletionRate());
                } catch (Exception e) {
                    System.out.println("操作失敗: " + e.getMessage());
                }
            } else if ("6".equals(choice)) {
                if (tasks.isEmpty()) {
                    System.out.println("此專案目前沒有任務。");
                } else {
                    System.out.println("任務清單:");
                    for (int i = 0; i < tasks.size(); i++) {
                        Task t = tasks.get(i);
                        System.out.println((i + 1) + ") [" + t.getClass().getSimpleName() + "] "
                                + t.getId() + " - " + t.getTitle() + " (status=" + t.getStatus() + ")");
                    }
                }
            } else if ("7".equals(choice)) {
                if (tasks.isEmpty()) {
                    System.out.println("此專案目前沒有任務。");
                    continue;
                }
                System.out.println("選擇要變更的任務編號:");
                for (int i = 0; i < tasks.size(); i++) {
                    Task t = tasks.get(i);
                    System.out.println((i + 1) + ") " + t.getId() + " (目前狀態=" + t.getStatus() + ")");
                }
                System.out.print("輸入編號: ");
                String idxStr = scanner.nextLine();
                try {
                    int idx = Integer.parseInt(idxStr) - 1;
                    if (idx < 0 || idx >= tasks.size()) {
                        System.out.println("無效編號。");
                        continue;
                    }
                    Task task = tasks.get(idx);
                    System.out.println("選擇新狀態: 1) TODO  2) IN_PROGRESS  3) IN_REVIEW  4) BLOCKED  5) DONE");
                    String st = scanner.nextLine().trim();
                    TaskStatus newStatus = TaskStatus.TODO;
                    if ("1".equals(st)) newStatus = TaskStatus.TODO;
                    else if ("2".equals(st)) newStatus = TaskStatus.IN_PROGRESS;
                    else if ("3".equals(st)) newStatus = TaskStatus.IN_REVIEW;
                    else if ("4".equals(st)) newStatus = TaskStatus.BLOCKED;
                    else if ("5".equals(st)) newStatus = TaskStatus.DONE;
                    try {
                        // 目前系統尚未將 User 與 TeamMember 打通，因此這裡暫以 null 作為 changedBy
                        boolean ok = task.transitionTo(newStatus, null);
                        System.out.println("狀態變更結果: " + ok + "，目前狀態=" + task.getStatus());
                    } catch (Exception e) {
                        System.out.println("狀態變更失敗: " + e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("請輸入數字編號。");
                }
            } else if ("8".equals(choice)) {
                if (tasks.isEmpty()) {
                    System.out.println("此專案目前沒有任務。");
                    continue;
                }
                if (project.getMembers().isEmpty()) {
                    System.out.println("此專案尚無成員，請先加入成員。");
                    continue;
                }
                Task task = pickAnyTask(scanner, tasks);
                if (task == null) continue;
                System.out.println("1) 指派  2) 取消指派");
                String assignChoice = scanner.nextLine().trim();
                if ("1".equals(assignChoice)) {
                    TeamMember member = pickMember(scanner, project.getMembers());
                    if (member == null) continue;
                    try {
                        task.assignTo(member);
                        System.out.println("已指派任務 " + task.getId() + " 給 " + member.getName());
                    } catch (Exception e) {
                        System.out.println("指派失敗: " + e.getMessage());
                    }
                } else if ("2".equals(assignChoice)) {
                    task.unassign();
                    System.out.println("已取消指派。");
                } else {
                    System.out.println("無效選項。");
                }
            } else if ("9".equals(choice)) {
                manageMembers(scanner, project);
            } else if ("10".equals(choice)) {
                boardMenu(scanner, board, tasks);
            } else if ("11".equals(choice)) {
                sprint = sprintMenu(scanner, sprint, tasks);
            } else if ("12".equals(choice)) {
                demoCommentWorkLog();
            } else if ("13".equals(choice)) {
                observerDemo(scanner, tasks);
            } else if ("14".equals(choice)) {
                System.out.println("離開專案：" + project.getName());
                return;
            } else {
                System.out.println("無效選項，請重新輸入。");
            }
        }
    }

    private static void manageMembers(Scanner scanner, Project project) {
        while (true) {
            System.out.println("== 成員管理 ==");
            System.out.println("1) 新增成員");
            System.out.println("2) 列出成員");
            System.out.println("3) 返回");
            System.out.print("請選擇: ");
            String c = scanner.nextLine().trim();
            if ("1".equals(c)) {
                System.out.println("選擇角色: 1) Developer  2) Tester  3) ProjectManager  4) ProductOwner");
                String role = scanner.nextLine().trim();
                System.out.print("輸入 memberId: ");
                String id = scanner.nextLine();
                System.out.print("輸入 name: ");
                String name = scanner.nextLine();
                try {
                    TeamMember m;
                    if ("1".equals(role)) m = new Developer(id, name);
                    else if ("2".equals(role)) m = new Tester(id, name);
                    else if ("3".equals(role)) m = new ProjectManager(id, name);
                    else if ("4".equals(role)) m = new ProductOwner(id, name);
                    else {
                        System.out.println("無效角色。");
                        continue;
                    }
                    project.addMember(m);
                    System.out.println("已加入成員: " + m.getMemberId() + " / " + m.getName() + " (" + m.getRole() + ")");
                } catch (Exception e) {
                    System.out.println("新增失敗: " + e.getMessage());
                }
            } else if ("2".equals(c)) {
                if (project.getMembers().isEmpty()) {
                    System.out.println("目前沒有成員。");
                } else {
                    for (TeamMember m : project.getMembers()) {
                        System.out.println("  - " + m.getMemberId() + " / " + m.getName() + " (" + m.getRole() + ")");
                    }
                }
            } else if ("3".equals(c)) {
                return;
            } else {
                System.out.println("無效選項。");
            }
        }
    }

    private static void boardMenu(Scanner scanner, Board board, List<Task> tasks) {
        while (true) {
            System.out.println("== 看板操作 ==");
            System.out.println("1) 新增欄位 (TaskStatus)");
            System.out.println("2) 將任務加入看板");
            System.out.println("3) 依狀態查詢任務");
            System.out.println("4) 返回");
            System.out.print("請選擇: ");
            String c = scanner.nextLine().trim();
            if ("1".equals(c)) {
                TaskStatus status = pickStatus(scanner);
                if (status != null) {
                    board.addColumn(status);
                    System.out.println("已新增欄位: " + status);
                }
            } else if ("2".equals(c)) {
                Task task = pickAnyTask(scanner, tasks);
                if (task != null) {
                    board.addTask(task);
                    System.out.println("已加入任務 " + task.getId() + " 到看板");
                }
            } else if ("3".equals(c)) {
                TaskStatus status = pickStatus(scanner);
                if (status != null) {
                    List<Task> result = board.getTasksByStatus(status);
                    System.out.println("狀態 " + status + " 任務數: " + result.size());
                    for (Task t : result) {
                        System.out.println("  - " + t.getId() + " (" + t.getStatus() + ")");
                    }
                }
            } else if ("4".equals(c)) {
                return;
            } else {
                System.out.println("無效選項。");
            }
        }
    }

    private static Sprint sprintMenu(Scanner scanner, Sprint sprint, List<Task> tasks) {
        while (true) {
            System.out.println("== 衝刺操作 ==");
            System.out.println("1) 建立/重設 Sprint");
            System.out.println("2) 將任務加入 Sprint");
            System.out.println("3) 查看剩餘任務數");
            System.out.println("4) 查看 BurnDown 資料");
            System.out.println("5) 返回");
            System.out.print("請選擇: ");
            String c = scanner.nextLine().trim();
            if ("1".equals(c)) {
                System.out.print("輸入 startDate (yyyy-MM-dd): ");
                String s = scanner.nextLine();
                System.out.print("輸入 endDate (yyyy-MM-dd): ");
                String e = scanner.nextLine();
                sprint = new Sprint(s, e);
                System.out.println("已建立 Sprint: " + s + " ~ " + e);
            } else if ("2".equals(c)) {
                if (sprint == null) {
                    System.out.println("請先建立 Sprint。");
                    continue;
                }
                Task task = pickAnyTask(scanner, tasks);
                if (task != null) {
                    sprint.addTask(task);
                    System.out.println("已加入任務 " + task.getId() + " 到 Sprint");
                }
            } else if ("3".equals(c)) {
                if (sprint == null) {
                    System.out.println("請先建立 Sprint。");
                    continue;
                }
                System.out.println("剩餘任務數: " + sprint.getRemainingTasksCount());
            } else if ("4".equals(c)) {
                if (sprint == null) {
                    System.out.println("請先建立 Sprint。");
                    continue;
                }
                System.out.println("BurnDown 資料: " + sprint.calculateBurnDownData());
            } else if ("5".equals(c)) {
                return sprint;
            } else {
                System.out.println("無效選項。");
            }
        }
    }

    private static void demoCommentWorkLog() {
        TeamMember author = new Developer("U001", "Alice");
        Comment comment = new Comment(author, "LGTM", "2025-12-22 10:00");
        WorkLog log = new WorkLog(author, 3, "Fix bug #12");
        System.out.println("已建立 Comment 範例: author=" + comment.getAuthor().getName() +
                ", content=" + comment.getContent() + ", time=" + comment.getCreatedTime());
        System.out.println("已建立 WorkLog 範例: member=" + log.getMember().getName() +
                ", hours=" + log.getHours() + ", desc=" + log.getDescription());
    }

    private static Task pickTaskByType(Scanner scanner, List<Task> tasks, Class<?> type) {
        List<Task> filtered = new ArrayList<>();
        for (Task t : tasks) {
            if (type.isInstance(t)) {
                filtered.add(t);
            }
        }
        if (filtered.isEmpty()) {
            System.out.println("找不到類型 " + type.getSimpleName() + " 的任務。");
            return null;
        }
        System.out.println("選擇任務編號:");
        for (int i = 0; i < filtered.size(); i++) {
            Task t = filtered.get(i);
            System.out.println((i + 1) + ") " + t.getId() + " (" + t.getTitle() + ")");
        }
        System.out.print("輸入編號: ");
        String idxStr = scanner.nextLine();
        try {
            int idx = Integer.parseInt(idxStr) - 1;
            if (idx < 0 || idx >= filtered.size()) {
                System.out.println("無效編號。");
                return null;
            }
            return filtered.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字。");
            return null;
        }
    }

    private static Task pickAnyTask(Scanner scanner, List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("沒有任務可選。");
            return null;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            System.out.println((i + 1) + ") " + t.getId() + " (" + t.getTitle() + "), status=" + t.getStatus());
        }
        System.out.print("輸入編號: ");
        String idxStr = scanner.nextLine();
        try {
            int idx = Integer.parseInt(idxStr) - 1;
            if (idx < 0 || idx >= tasks.size()) {
                System.out.println("無效編號。");
                return null;
            }
            return tasks.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字。");
            return null;
        }
    }

    private static TeamMember pickMember(Scanner scanner, List<TeamMember> members) {
        if (members.isEmpty()) {
            System.out.println("沒有成員可選。");
            return null;
        }
        for (int i = 0; i < members.size(); i++) {
            TeamMember m = members.get(i);
            System.out.println((i + 1) + ") " + m.getMemberId() + " / " + m.getName() + " (" + m.getRole() + ")");
        }
        System.out.print("輸入編號: ");
        String idxStr = scanner.nextLine();
        try {
            int idx = Integer.parseInt(idxStr) - 1;
            if (idx < 0 || idx >= members.size()) {
                System.out.println("無效編號。");
                return null;
            }
            return members.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("請輸入數字。");
            return null;
        }
    }

    private static TaskStatus pickStatus(Scanner scanner) {
        System.out.println("選擇狀態: 1) TODO  2) IN_PROGRESS  3) IN_REVIEW  4) BLOCKED  5) DONE");
        String st = scanner.nextLine().trim();
        if ("1".equals(st)) return TaskStatus.TODO;
        if ("2".equals(st)) return TaskStatus.IN_PROGRESS;
        if ("3".equals(st)) return TaskStatus.IN_REVIEW;
        if ("4".equals(st)) return TaskStatus.BLOCKED;
        if ("5".equals(st)) return TaskStatus.DONE;
        System.out.println("無效選項。");
        return null;
    }

    private static void observerDemo(Scanner scanner, List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("沒有任務可用於觀察者示範。");
            return;
        }
        Task task = pickAnyTask(scanner, tasks);
        if (task == null) return;
        final List<StatusChange> received = new ArrayList<>();
        TaskObserver observer = (t, change) -> {
            received.add(change);
            System.out.println("[Observer] 任務 " + t.getId() + " 狀態變更: " + change);
        };
        task.addObserver(observer);
        System.out.println("已替任務 " + task.getId() + " 註冊觀察者。選擇狀態觸發通知。");
        TaskStatus status = pickStatus(scanner);
        if (status != null) {
            try {
                task.transitionTo(status, null);
            } catch (Exception e) {
                System.out.println("轉換失敗: " + e.getMessage());
            }
        }
        System.out.println("觀察者收到通知次數: " + received.size());
    }
    // =================== 測試案例模式（原本的 Test Runner） ===================

    private static void runTestRunner(Scanner scanner) {
        System.out.println("==== Project Management System Test Runner ====");
        System.out.println("請輸入測試案例編號 (例如: TC-E001)，或輸入 LIST 查看清單，輸入 EXIT 離開。");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null) {
                break;
            }
            line = line.trim().toUpperCase();
            if ("EXIT".equals(line)) {
                System.out.println("結束測試。");
                break;
            }
            if ("LIST".equals(line)) {
                printTestCaseList();
                continue;
            }

            runTestCase(line);
        }
    }

    private static void printTestCaseList() {
        System.out.println("可用測試案例：");
        System.out.println("Enum:      TC-E001, TC-E002");
        System.out.println("Task:      TC-T001 ~ TC-T006");
        System.out.println("Transition:TC-TS001 ~ TC-TS010");
        System.out.println("Assign:    TC-A001 ~ TC-A003");
        System.out.println("Story:     TC-S001 ~ TC-S004");
        System.out.println("Bug:       TC-BG001 ~ TC-BG002");
        System.out.println("Epic:      TC-EP001 ~ TC-EP004");
        System.out.println("SubTask:   TC-ST001");
        System.out.println("TeamMember:TC-TM001 ~ TC-TM004");
        System.out.println("Project:   TC-P001 ~ TC-P004");
        System.out.println("Board:     TC-BD001 ~ TC-BD002");
        System.out.println("Sprint:    TC-SP001 ~ TC-SP002");
        System.out.println("StatusChange: TC-SC001");
        System.out.println("Observer:  TC-OB001 ~ TC-OB002");
        System.out.println("Comment:   TC-CM001");
        System.out.println("WorkLog:   TC-WL001");
    }

    private static void runTestCase(String id) {
        try {
            switch (id) {
                case "TC-E001":
                    testEnumTaskStatus();
                    break;
                case "TC-E002":
                    testEnumPriority();
                    break;
                case "TC-T001":
                    testTaskCreateNormal();
                    break;
                case "TC-T002":
                    testTaskIdNull();
                    break;
                case "TC-T003":
                    testTaskIdEmpty();
                    break;
                case "TC-T004":
                    testTaskTitleNull();
                    break;
                case "TC-T005":
                    testTaskTitleEmpty();
                    break;
                case "TC-T006":
                    testTaskGetStatus();
                    break;
                case "TC-TS001":
                case "TC-TS002":
                case "TC-TS003":
                case "TC-TS004":
                case "TC-TS005":
                case "TC-TS006":
                case "TC-TS007":
                case "TC-TS008":
                case "TC-TS009":
                case "TC-TS010":
                    testTransitions(id);
                    break;
                case "TC-A001":
                    testAssignSuccess();
                    break;
                case "TC-A002":
                    testAssignNull();
                    break;
                case "TC-A003":
                    testUnassign();
                    break;
                case "TC-S001":
                    testStoryAddSubTask();
                    break;
                case "TC-S002":
                    testStoryAddSubTaskNull();
                    break;
                case "TC-S003":
                    testStoryProgressHalf();
                    break;
                case "TC-S004":
                    testStoryProgressEmpty();
                    break;
                case "TC-BG001":
                    testBugSetSeverity();
                    break;
                case "TC-BG002":
                    testBugSetSeverityNull();
                    break;
                case "TC-EP001":
                    testEpicAddStory();
                    break;
                case "TC-EP002":
                    testEpicAddStoryNull();
                    break;
                case "TC-EP003":
                    testEpicCompletionRateHalf();
                    break;
                case "TC-EP004":
                    testEpicCompletionRateEmpty();
                    break;
                case "TC-ST001":
                    testSubTaskConstraint();
                    break;
                case "TC-TM001":
                    testTeamMemberCreate();
                    break;
                case "TC-TM002":
                    testTeamMemberIdEmpty();
                    break;
                case "TC-TM003":
                    testTeamMemberNameEmpty();
                    break;
                case "TC-TM004":
                    testTeamMemberSubclasses();
                    break;
                case "TC-P001":
                    testProjectAddTask();
                    break;
                case "TC-P002":
                    testProjectAddTaskNull();
                    break;
                case "TC-P003":
                    testProjectAddMember();
                    break;
                case "TC-P004":
                    testProjectAddMemberDuplicate();
                    break;
                case "TC-BD001":
                    testBoardGetTasksByStatus();
                    break;
                case "TC-BD002":
                    testBoardGetTasksByStatusEmpty();
                    break;
                case "TC-SP001":
                    testSprintRemainingTasks();
                    break;
                case "TC-SP002":
                    testSprintBurnDownData();
                    break;
                case "TC-SC001":
                    testStatusChangeContent();
                    break;
                case "TC-OB001":
                    testObserverNotifiedOnLegalTransition();
                    break;
                case "TC-OB002":
                    testObserverNotNotifiedOnIllegalTransition();
                    break;
                case "TC-CM001":
                    testCommentCreate();
                    break;
                case "TC-WL001":
                    testWorkLogCreate();
                    break;
                default:
                    System.out.println("未知的測試案例編號：" + id);
            }
        } catch (Exception e) {
            System.out.println("執行測試案例時發生未預期例外：" + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
        System.out.println("--------------------------------------------------");
    }

    // Enum 測試
    private static void testEnumTaskStatus() {
        System.out.println("TC-E001: TaskStatus 應包含 5 種狀態");
        for (TaskStatus s : TaskStatus.values()) {
            System.out.println("  status: " + s);
        }
    }

    private static void testEnumPriority() {
        System.out.println("TC-E002: Priority 應包含 4 種等級");
        for (Priority p : Priority.values()) {
            System.out.println("  priority: " + p);
        }
    }

    // Task 建構與 getter
    private static void testTaskCreateNormal() {
        System.out.println("TC-T001: 正常建立 Task（以 Story 具體類別建立）");
        Story story = new Story("S-001", "Login UI", "Create login screen");
        System.out.println("  id=" + story.getId());
        System.out.println("  title=" + story.getTitle());
        System.out.println("  description=" + story.getDescription());
        System.out.println("  status=" + story.getStatus());
        System.out.println("  priority=" + story.getPriority());
        System.out.println("  assignee=" + story.getAssignee());
        System.out.println("  statusHistory size=" + story.getStatusHistory().size());
    }

    private static void testTaskIdNull() {
        System.out.println("TC-T002: 建立 Task 時 id 為 null 拋出例外");
        try {
            new Story(null, "Login UI", "...");
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testTaskIdEmpty() {
        System.out.println("TC-T003: 建立 Task 時 id 為空字串拋出例外");
        try {
            new Story("", "Login UI", "...");
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testTaskTitleNull() {
        System.out.println("TC-T004: 建立 Task 時 title 為 null 拋出例外");
        try {
            new Story("S-001", null, "...");
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testTaskTitleEmpty() {
        System.out.println("TC-T005: 建立 Task 時 title 為空字串拋出例外");
        try {
            new Story("S-001", "", "...");
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testTaskGetStatus() {
        System.out.println("TC-T006: getStatus() 回傳目前狀態");
        Story story = new Story("S-001", "Login UI", "...");
        story.transitionTo(TaskStatus.IN_PROGRESS, null);
        System.out.println("  status=" + story.getStatus());
    }

    // 狀態轉換測試們
    private static void testTransitions(String id) {
        Story story = new Story("S-TS", "Transition test", "...");
        System.out.println(id + ": 狀態轉換測試，初始狀態=" + story.getStatus());
        try {
            switch (id) {
                case "TC-TS001":
                    System.out.println("  action: TODO -> IN_PROGRESS");
                    System.out.println("  result=" + story.transitionTo(TaskStatus.IN_PROGRESS, null));
                    break;
                case "TC-TS002":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    System.out.println("  action: IN_PROGRESS -> IN_REVIEW");
                    System.out.println("  result=" + story.transitionTo(TaskStatus.IN_REVIEW, null));
                    break;
                case "TC-TS003":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    System.out.println("  action: IN_PROGRESS -> BLOCKED");
                    System.out.println("  result=" + story.transitionTo(TaskStatus.BLOCKED, null));
                    break;
                case "TC-TS004":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    story.transitionTo(TaskStatus.IN_REVIEW, null);
                    System.out.println("  action: IN_REVIEW -> DONE");
                    System.out.println("  result=" + story.transitionTo(TaskStatus.DONE, null));
                    break;
                case "TC-TS005":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    story.transitionTo(TaskStatus.IN_REVIEW, null);
                    System.out.println("  action: IN_REVIEW -> IN_PROGRESS");
                    System.out.println("  result=" + story.transitionTo(TaskStatus.IN_PROGRESS, null));
                    break;
                case "TC-TS006":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    story.transitionTo(TaskStatus.BLOCKED, null);
                    System.out.println("  action: BLOCKED -> IN_PROGRESS");
                    System.out.println("  result=" + story.transitionTo(TaskStatus.IN_PROGRESS, null));
                    break;
                case "TC-TS007":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    story.transitionTo(TaskStatus.IN_REVIEW, null);
                    story.transitionTo(TaskStatus.DONE, null);
                    System.out.println("  action: DONE -> IN_PROGRESS");
                    System.out.println("  result=" + story.transitionTo(TaskStatus.IN_PROGRESS, null));
                    break;
                case "TC-TS008":
                    System.out.println("  action: TODO -> DONE (非法)");
                    story.transitionTo(TaskStatus.DONE, null);
                    break;
                case "TC-TS009":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    story.transitionTo(TaskStatus.BLOCKED, null);
                    System.out.println("  action: BLOCKED -> DONE (非法)");
                    story.transitionTo(TaskStatus.DONE, null);
                    break;
                case "TC-TS010":
                    story.transitionTo(TaskStatus.IN_PROGRESS, null);
                    story.transitionTo(TaskStatus.IN_REVIEW, null);
                    story.transitionTo(TaskStatus.DONE, null);
                    System.out.println("  action: DONE -> DONE (非法)");
                    story.transitionTo(TaskStatus.DONE);
                    break;
            }
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
        System.out.println("  statusHistory size=" + story.getStatusHistory().size());
    }

    // assign / unassign
    private static void testAssignSuccess() {
        System.out.println("TC-A001: 指派成員成功");
        Story story = new Story("S-001", "Login UI", "...");
        TeamMember member = new Developer("U001", "Alice");
        story.assignTo(member);
        System.out.println("  assignee=" + story.getAssignee().getName());
    }

    private static void testAssignNull() {
        System.out.println("TC-A002: assignTo 傳入 null 拋出例外");
        Story story = new Story("S-001", "Login UI", "...");
        try {
            story.assignTo(null);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testUnassign() {
        System.out.println("TC-A003: 取消指派成功");
        Story story = new Story("S-001", "Login UI", "...");
        TeamMember member = new Developer("U001", "Alice");
        story.assignTo(member);
        story.unassign();
        System.out.println("  assignee=" + story.getAssignee());
    }

    // Story
    private static void testStoryAddSubTask() {
        System.out.println("TC-S001: addSubTask 成功加入子任務");
        Story story = new Story("S-1", "Story", "...");
        SubTask sub = new SubTask("ST-1", "Sub", "...", story);
        story.addSubTask(sub);
        System.out.println("  subTasks size=" + story.getSubTasks().size());
    }

    private static void testStoryAddSubTaskNull() {
        System.out.println("TC-S002: addSubTask 傳入 null 拋出例外");
        Story story = new Story("S-1", "Story", "...");
        try {
            story.addSubTask(null);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testStoryProgressHalf() {
        System.out.println("TC-S003: getProgress 計算完成比例（0.0 ~ 1.0）");
        Story story = new Story("S-1", "Story", "...");
        SubTask st1 = new SubTask("ST-1", "Sub1", "...", story);
        SubTask st2 = new SubTask("ST-2", "Sub2", "...", story);
        SubTask st3 = new SubTask("ST-3", "Sub3", "...", story);
        SubTask st4 = new SubTask("ST-4", "Sub4", "...", story);
        story.addSubTask(st1);
        story.addSubTask(st2);
        story.addSubTask(st3);
        story.addSubTask(st4);
        // 合法路徑：TODO -> IN_PROGRESS -> IN_REVIEW -> DONE
        st1.transitionTo(TaskStatus.IN_PROGRESS, null);
        st1.transitionTo(TaskStatus.IN_REVIEW, null);
        st1.transitionTo(TaskStatus.DONE, null);
        st2.transitionTo(TaskStatus.IN_PROGRESS, null);
        st2.transitionTo(TaskStatus.IN_REVIEW, null);
        st2.transitionTo(TaskStatus.DONE, null);
        System.out.println("  progress=" + story.getProgress());
    }

    private static void testStoryProgressEmpty() {
        System.out.println("TC-S004: getProgress 當沒有任何 SubTask");
        Story story = new Story("S-1", "Story", "...");
        System.out.println("  progress=" + story.getProgress());
    }

    // Bug
    private static void testBugSetSeverity() {
        System.out.println("TC-BG001: setSeverity 設定嚴重程度成功");
        Bug bug = new Bug("B-1", "Bug", "...", Priority.LOW);
        bug.setSeverity(Priority.HIGH);
        System.out.println("  severity=" + bug.getSeverity());
    }

    private static void testBugSetSeverityNull() {
        System.out.println("TC-BG002: setSeverity 傳入 null 拋出例外");
        Bug bug = new Bug("B-1", "Bug", "...", Priority.LOW);
        try {
            bug.setSeverity(null);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    // Epic
    private static void testEpicAddStory() {
        System.out.println("TC-EP001: addStory 加入 Story 成功");
        Epic epic = new Epic("E-1", "Epic", "...");
        Story story = new Story("S-1", "Story", "...");
        epic.addStory(story);
        System.out.println("  stories size=" + epic.getStories().size());
    }

    private static void testEpicAddStoryNull() {
        System.out.println("TC-EP002: addStory 傳入 null 拋出例外");
        Epic epic = new Epic("E-1", "Epic", "...");
        try {
            epic.addStory(null);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testEpicCompletionRateHalf() {
        System.out.println("TC-EP003: getCompletionRate 回傳所有 Story 平均完成度");
        Epic epic = new Epic("E-1", "Epic", "...");

        // Story1 progress = 0.25 (4 個 SubTask，其中 1 個 DONE)
        Story s1 = new Story("S-1", "S1", "...");
        SubTask s1a = new SubTask("ST-1", "Sub1", "...", s1);
        SubTask s1b = new SubTask("ST-2", "Sub2", "...", s1);
        SubTask s1c = new SubTask("ST-3", "Sub3", "...", s1);
        SubTask s1d = new SubTask("ST-4", "Sub4", "...", s1);
        s1.addSubTask(s1a);
        s1.addSubTask(s1b);
        s1.addSubTask(s1c);
        s1.addSubTask(s1d);
        s1a.transitionTo(TaskStatus.IN_PROGRESS, null);
        s1a.transitionTo(TaskStatus.IN_REVIEW, null);
        s1a.transitionTo(TaskStatus.DONE, null); // 1 / 4 = 0.25

        // Story2 progress = 0.75 (4 個 SubTask，其中 3 個 DONE)
        Story s2 = new Story("S-2", "S2", "...");
        SubTask s2a = new SubTask("ST-5", "Sub1", "...", s2);
        SubTask s2b = new SubTask("ST-6", "Sub2", "...", s2);
        SubTask s2c = new SubTask("ST-7", "Sub3", "...", s2);
        SubTask s2d = new SubTask("ST-8", "Sub4", "...", s2);
        s2.addSubTask(s2a);
        s2.addSubTask(s2b);
        s2.addSubTask(s2c);
        s2.addSubTask(s2d);
        s2a.transitionTo(TaskStatus.IN_PROGRESS, null);
        s2a.transitionTo(TaskStatus.IN_REVIEW, null);
        s2a.transitionTo(TaskStatus.DONE, null);
        s2b.transitionTo(TaskStatus.IN_PROGRESS, null);
        s2b.transitionTo(TaskStatus.IN_REVIEW, null);
        s2b.transitionTo(TaskStatus.DONE, null);
        s2c.transitionTo(TaskStatus.IN_PROGRESS, null);
        s2c.transitionTo(TaskStatus.IN_REVIEW, null);
        s2c.transitionTo(TaskStatus.DONE, null); // 3 / 4 = 0.75

        epic.addStory(s1);
        epic.addStory(s2);
        System.out.println("  completionRate=" + epic.getCompletionRate());
    }

    private static void testEpicCompletionRateEmpty() {
        System.out.println("TC-EP004: getCompletionRate 當 Epic 沒有 Story");
        Epic epic = new Epic("E-1", "Epic", "...");
        System.out.println("  completionRate=" + epic.getCompletionRate());
    }

    // SubTask constraint
    private static void testSubTaskConstraint() {
        System.out.println("TC-ST001: SubTask 必須依附某一 Story（結構約束）");
        try {
            new SubTask("ST-1", "Orphan", "...", null);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    // TeamMember
    private static void testTeamMemberCreate() {
        System.out.println("TC-TM001: 正常建立 TeamMember");
        TeamMember m = new TeamMember("U001", "Alice", "Developer");
        System.out.println("  id=" + m.getMemberId());
        System.out.println("  name=" + m.getName());
        System.out.println("  role=" + m.getRole());
    }

    private static void testTeamMemberIdEmpty() {
        System.out.println("TC-TM002: memberId 為空字串不允許");
        try {
            new TeamMember("", "Alice", "Developer");
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testTeamMemberNameEmpty() {
        System.out.println("TC-TM003: name 為空字串不允許");
        try {
            new TeamMember("U001", "", "Developer");
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testTeamMemberSubclasses() {
        System.out.println("TC-TM004: 子類別可正確建立");
        List<TeamMember> members = new ArrayList<>();
        members.add(new Developer("D1", "Dev"));
        members.add(new Tester("T1", "Tester"));
        members.add(new ProjectManager("PM1", "PM"));
        members.add(new ProductOwner("PO1", "PO"));
        for (TeamMember m : members) {
            System.out.println("  member: id=" + m.getMemberId() + ", name=" + m.getName() + ", role=" + m.getRole());
        }
    }

    // Project
    private static void testProjectAddTask() {
        System.out.println("TC-P001: addTask 成功加入任務");
        Project p = new Project("Demo");
        Story s = new Story("S-1", "Story", "...");
        p.addTask(s);
        System.out.println("  tasks size=" + p.getTasks().size());
    }

    private static void testProjectAddTaskNull() {
        System.out.println("TC-P002: addTask 傳入 null 拋出例外");
        Project p = new Project("Demo");
        try {
            p.addTask(null);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    private static void testProjectAddMember() {
        System.out.println("TC-P003: addMember 成功加入成員");
        Project p = new Project("Demo");
        TeamMember m = new Developer("U001", "Alice");
        p.addMember(m);
        System.out.println("  members size=" + p.getMembers().size());
    }

    private static void testProjectAddMemberDuplicate() {
        System.out.println("TC-P004: addMember 加入重複成員拋出例外");
        Project p = new Project("Demo");
        TeamMember m1 = new Developer("U001", "Alice");
        TeamMember m2 = new Tester("U001", "Bob");
        p.addMember(m1);
        try {
            p.addMember(m2);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
    }

    // Board
    private static void testBoardGetTasksByStatus() {
        System.out.println("TC-BD001: getTasksByStatus 回傳符合狀態的任務");
        Board board = new Board();
        Story s1 = new Story("S-1", "Story1", "...");
        Story s2 = new Story("S-2", "Story2", "...");
        Story s3 = new Story("S-3", "Story3", "...");
        // 前置：3 個任務，其中 1 個 IN_PROGRESS、2 個維持預設 TODO
        s1.transitionTo(TaskStatus.IN_PROGRESS, null);
        board.addTask(s1);
        board.addTask(s2);
        board.addTask(s3);
        List<Task> todos = board.getTasksByStatus(TaskStatus.TODO);
        System.out.println("  TODO size=" + todos.size());
        for (Task t : todos) {
            System.out.println("    task id=" + t.getId() + ", status=" + t.getStatus());
        }
    }

    private static void testBoardGetTasksByStatusEmpty() {
        System.out.println("TC-BD002: getTasksByStatus 查無結果");
        Board board = new Board();
        Story s1 = new Story("S-1", "Story1", "...");
        s1.transitionTo(TaskStatus.IN_PROGRESS);
        board.addTask(s1);
        List<Task> blocked = board.getTasksByStatus(TaskStatus.BLOCKED);
        System.out.println("  BLOCKED size=" + blocked.size());
    }

    // Sprint
    private static void testSprintRemainingTasks() {
        System.out.println("TC-SP001: getRemainingTasksCount 回傳非 DONE 的任務數");
        Sprint sprint = new Sprint("2025-01-01", "2025-01-05");
        Story t1 = new Story("T1", "Task1", "...");
        Story t2 = new Story("T2", "Task2", "...");
        Story t3 = new Story("T3", "Task3", "...");
        Story t4 = new Story("T4", "Task4", "...");
        Story t5 = new Story("T5", "Task5", "...");
        sprint.addTask(t1);
        sprint.addTask(t2);
        sprint.addTask(t3);
        sprint.addTask(t4);
        sprint.addTask(t5);
        t4.transitionTo(TaskStatus.IN_PROGRESS, null);
        t4.transitionTo(TaskStatus.IN_REVIEW, null);
        t4.transitionTo(TaskStatus.DONE, null);
        t5.transitionTo(TaskStatus.IN_PROGRESS, null);
        t5.transitionTo(TaskStatus.IN_REVIEW, null);
        t5.transitionTo(TaskStatus.DONE, null);
        System.out.println("  remaining=" + sprint.getRemainingTasksCount());
    }

    private static void testSprintBurnDownData() {
        System.out.println("TC-SP002: calculateBurnDownData 回傳每日剩餘任務數資料");
        Sprint sprint = new Sprint("2025-01-01", "2025-01-05");
        Story t1 = new Story("T1", "Task1", "...");
        Story t2 = new Story("T2", "Task2", "...");
        Story t3 = new Story("T3", "Task3", "...");
        sprint.addTask(t1);
        sprint.addTask(t2);
        sprint.addTask(t3);
        // 這裡不模擬真實每日完成，只輸出目前的 burn down 資料讓使用者人工檢視
        System.out.println("  burnDownData=" + sprint.calculateBurnDownData());
    }

    // StatusChange
    private static void testStatusChangeContent() {
        System.out.println("TC-SC001: 狀態變更紀錄內容正確");
        TeamMember member = new Developer("U001", "Alice");
        Story story = new Story("S-1", "Story", "...");
        story.assignTo(member);
        story.transitionTo(TaskStatus.IN_PROGRESS, member);
        List<StatusChange> history = story.getStatusHistory();
        StatusChange latest = history.get(history.size() - 1);
        System.out.println("  fromStatus=" + latest.getFromStatus());
        System.out.println("  toStatus=" + latest.getToStatus());
        System.out.println("  changedBy=" + (latest.getChangedBy() == null ? null : latest.getChangedBy().getName()));
        System.out.println("  timestamp=" + latest.getTimestamp());
    }

    // Observer
    private static void testObserverNotifiedOnLegalTransition() {
        System.out.println("TC-OB001: 任務狀態成功變更時會通知觀察者");
        Story story = new Story("S-1", "Story", "...");
        final List<StatusChange> received = new ArrayList<>();
        TaskObserver observer = (task, change) -> {
            System.out.println("  Observer called for task=" + task.getId() + ", change=" + change);
            received.add(change);
        };
        story.addObserver(observer);
        story.transitionTo(TaskStatus.IN_PROGRESS);
        System.out.println("  observer call count=" + received.size());
    }

    private static void testObserverNotNotifiedOnIllegalTransition() {
        System.out.println("TC-OB002: 非法狀態轉換不應通知觀察者");
        Story story = new Story("S-1", "Story", "...");
        final List<StatusChange> received = new ArrayList<>();
        TaskObserver observer = (task, change) -> received.add(change);
        story.addObserver(observer);
        try {
            story.transitionTo(TaskStatus.DONE);
        } catch (Exception e) {
            System.out.println("  exception=" + e.getClass().getSimpleName() + ", message=" + e.getMessage());
        }
        System.out.println("  observer call count=" + received.size());
        System.out.println("  statusHistory size=" + story.getStatusHistory().size());
    }

    // Comment
    private static void testCommentCreate() {
        System.out.println("TC-CM001: 正常建立 Comment");
        TeamMember author = new Developer("U001", "Alice");
        Comment comment = new Comment(author, "LGTM", "2025-12-22 10:00");
        System.out.println("  author=" + comment.getAuthor().getName());
        System.out.println("  content=" + comment.getContent());
        System.out.println("  createdTime=" + comment.getCreatedTime());
    }

    // WorkLog
    private static void testWorkLogCreate() {
        System.out.println("TC-WL001: 正常建立 WorkLog");
        TeamMember member = new Developer("U001", "Alice");
        WorkLog log = new WorkLog(member, 3, "Fix bug #12");
        System.out.println("  member=" + log.getMember().getName());
        System.out.println("  hours=" + log.getHours());
        System.out.println("  description=" + log.getDescription());
    }
}
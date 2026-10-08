package library.repository;

import java.util.*;
import java.io.IOException;

import library.model.Member;
import library.util.TsvIO;

public class MemberRepository {
    private List<Member> members = new ArrayList<>();
    private String filePath;

    public MemberRepository(String filePath) {
        this.filePath = filePath;
    }

    public void loadFromFile() throws IOException {
        members.clear();
        List<String> lines = TsvIO.readLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\t");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];

            Member member = new Member(id, name);
            members.add(member);
        }
    }

    public void saveToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            String line = m.getId() + "\t"
                        + m.getName();
            lines.add(line);
        }
        TsvIO.writeLines(filePath, lines);
    }

    public void add(Member member) {
        members.add(member);
    }

    public List<Member> findAll() {
        return members;
    }

    public Member findById(int id) {
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public List<Member> findByName(String name) {
        List<Member> result = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            if (m.getName().contains(name)) {
                result.add(m);
            }
        }
        return result;
    }

    public void delete(int id) {
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            if (m.getId() == id) {
                members.remove(i);
                return;
            }
        }
    }

    public void update(Member member) {
        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);
            if (m.getId() == member.getId()) {
                members.set(i, member);
                return;
            }
        }
    }
}

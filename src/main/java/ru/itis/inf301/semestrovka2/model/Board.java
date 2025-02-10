package ru.itis.inf301.semestrovka2.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.min;

@Getter
@Setter
@ToString
public class Board {
    private int user0_x;
    private int user0_y;
    private int user1_x;
    private int user1_y;
    private int step;
    private int[][] vertical = new int[9][8];
    private int[][] horizontal = new int[8][9];


    public Board() {
        user0_x = 8;
        user0_y = 4;
        user1_x = 0;
        user1_y = 4;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 8; j++) {
                vertical[i][j] = 0;
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 9; j++) {
                horizontal[i][j] = 0;
            }
        }
        Random random = new Random();
        step = random.nextInt(2);
    }

    public Board(String input) {
        String[] parts = input.substring(6, input.length() - 1).replace(" ", "").split("vertical=");
        String[] pairs = parts[0].split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "user0_x":
                    this.user0_x = Integer.parseInt(value);
                    break;
                case "user0_y":
                    this.user0_y = Integer.parseInt(value);
                    break;
                case "user1_x":
                    this.user1_x = Integer.parseInt(value);
                    break;
                case "user1_y":
                    this.user1_y = Integer.parseInt(value);
                    break;
                case "step":
                    this.step = Integer.parseInt(value);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown key: " + key);
            }
        }
        String[] arrays = parts[1].split(",horizontal=");
        this.vertical = parseArray(arrays[0]);
        this.horizontal = parseArray(arrays[1]);
    }

    private int[][] parseArray(String arrayString) {
        // Удаляем внешние скобки
        arrayString = arrayString.substring(1, arrayString.length() - 1);
        // Разделяем на строки массива
        String[] rows = arrayString.split("\\],\\[");
        int[][] array = new int[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            // Удаляем лишние скобки и разделяем на элементы
            String row = rows[i].replace("[", "").replace("]", "");
            String[] elements = row.split(",");
            array[i] = new int[elements.length];

            for (int j = 0; j < elements.length; j++) {
                array[i][j] = Integer.parseInt(elements[j]);
            }
        }

        return array;
    }


    public int checkResult() {
        if (user0_y == 8) return 0;
        if (user1_y == 0) return 1;
        return -1;
    }

    public void doMove(int user, int x, int y) {
        if (user == 0) {
            user0_x = x;
            user0_y = y;
        } else {
            user1_x = x;
            user1_y = y;
        }
        step = (step + 1) % 2;
    }

    public boolean move(int user, int x, int y) {
        if (step != user) return false;
        int user_x;
        int user_y;
        int opponent_x;
        int opponent_y;
        if (user == 0) {
            user_x = user0_x;
            user_y = user0_y;
            opponent_x = user1_x;
            opponent_y = user1_y;
        } else {
            user_x = user1_x;
            user_y = user1_y;
            opponent_x = user0_x;
            opponent_y = user0_y;
        }
        int delta = abs(user_x - x) + abs(user_y - y);
        if (delta == 1) {
            if (abs(user_x - x) == 1) {
                if (horizontal[min(user_x, x)][y] == 0) {
                    if (x != opponent_x || y != opponent_y) {
                        doMove(user, x, user_y);
                        return true;
                    }
                }
            } else {
                if (vertical[x][min(user_y, y)] == 0) {
                    if (x != opponent_x || y != opponent_y) {
                        doMove(user, user_x, y);
                        return true;
                    }
                }
            }
        } else if (delta == 2) {
            if (abs(user_x - x) * abs(user_y - y) == 0) {
                if (abs(user_x - x) == 2) {
                    if ((x + user_x) / 2 == opponent_x && y == opponent_y) {
                        if (horizontal[min(user_x, opponent_x)][y] == 0 && horizontal[min(x, opponent_x)][y] == 0) {
                            doMove(user, x, user_y);
                            return true;
                        }
                    } else {
                        if ((y + user_y) / 2 == opponent_y && x == opponent_x) {
                            if (vertical[x][min(user_y, opponent_y)] == 0 && vertical[x][min(y, opponent_y)] == 0) {
                                doMove(user, user_x, y);
                                return true;
                            }
                        } else {
                            if (user_x == opponent_x && y == opponent_y) {
                                if (vertical[user_x][min(user_y, opponent_y)] == 0 && horizontal[min(x, opponent_x)][y] == 0) {
                                    doMove(user, x, y);
                                    return true;
                                }
                            } else if (user_y == opponent_y && x == opponent_x) {
                                if (vertical[x][min(y, opponent_y)] == 0 && horizontal[min(user_x, opponent_x)][user_y] == 0) {
                                    doMove(user, x, y);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean putHorizontalWall(int user, int x, int y) {
        if (step != user) return false;
        if (horizontal[x][y] == 0) {
            horizontal[x][y] = 1;
            step = (step + 1) % 2;
            return true;
        }
        return false;
    }

    public boolean putVerticalWall(int user, int x, int y) {
        if (step != user) return false;
        if (vertical[x][y] == 0) {
            vertical[x][y] = 1;
            step = (step + 1) % 2;
            return true;
        }
        return false;
    }
}

package salarycalculation.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import salarycalculation.database.CapabilityDao;
import salarycalculation.database.EmployeeDao;
import salarycalculation.database.RoleDao;
import salarycalculation.entity.Capability;
import salarycalculation.entity.Employee;
import salarycalculation.entity.Role;

/**
 * 社員情報リポジトリ。
 *
 * @author naotake
 */
public class EmployeeRepository {

    private EmployeeDao dao;
    private RoleDao roleDao;
    private CapabilityDao capabilityDao;

    public EmployeeRepository() {
        this.dao = new EmployeeDao();
        this.roleDao = new RoleDao();
        this.capabilityDao = new CapabilityDao();
    }

    /**
     * 社員番号を基に社員情報を取得する。
     *
     * @param no 社員番号
     * @return 社員情報
     */
    public EmployeeDomain get(String no) {
        Employee employee = dao.get(no);

        // 各等級情報を取得
        Role role = roleDao.get(employee.getRoleRank());
        Capability capability = capabilityDao.get(employee.getCapabilityRank());

        // Domain を準備
        EmployeeDomain domain = new EmployeeDomain(employee);
        domain.setRole(role);
        domain.setCapability(capability);

        return domain;
    }

    /**
     * 社員情報の一覧を取得する。<br />
     * 社員番号の昇順に一覧を取得する。
     *
     * @return 社員情報一覧
     */
    public List<EmployeeDomain> findAll() {
        List<Employee> employees = dao.findAll();

        // Domain 一覧を準備
        List<EmployeeDomain> domains = new ArrayList<>(employees.size());
        for (Employee employee : employees) {

            // 各等級情報を取得
            Role role = roleDao.get(employee.getRoleRank());
            Capability capability = capabilityDao.get(employee.getCapabilityRank());

            // Domain を準備
            EmployeeDomain domain = new EmployeeDomain(employee);
            domain.setRole(role);
            domain.setCapability(capability);

            domains.add(domain);
        }

        return domains;
    }

    /**
     * 想定年収順に社員情報の一覧を取得する。
     *
     * @param ascending 想定年収の昇順（低い順）なら true
     * @return 社員情報一覧
     */
    public List<EmployeeDomain> findAllOrderByAnnualSalary(boolean ascending) {
        List<Employee> employees = dao.findAll();

        // Domain 一覧を準備
        List<EmployeeDomain> domains = new ArrayList<>(employees.size());
        for (Employee employee : employees) {

            // 各等級情報を取得
            Role role = roleDao.get(employee.getRoleRank());
            Capability capability = capabilityDao.get(employee.getCapabilityRank());

            // Domain を準備
            EmployeeDomain domain = new EmployeeDomain(employee);
            domain.setRole(role);
            domain.setCapability(capability);

            domains.add(domain);
        }

        // 並び替え
        Collections.sort(domains, new Comparator<EmployeeDomain>() {

            @Override
            public int compare(EmployeeDomain o1, EmployeeDomain o2) {
                if (ascending) {
                    return (o1.getAnnualTotalSalaryPlan() - o2.getAnnualTotalSalaryPlan());
                } else {
                    return (o2.getAnnualTotalSalaryPlan() - o1.getAnnualTotalSalaryPlan());
                }
            }
        });

        return domains;
    }

    /**
     * 指定年月の全社員の総支給額の合計を取得する。
     *
     * @param yearMonth 算出対象の年月 (e.g. 201504)
     * @return 全社員の総支給額合計
     */
    public int getSumTotalSalary(int yearMonth) {
        List<EmployeeDomain> domains = findAll();

        int total = 0;
        for (EmployeeDomain domain : domains) {
            total += domain.getTotalSalary(yearMonth);
        }

        return total;
    }

    /**
     * 指定年月の全社員の手取り額の平均を取得する。
     *
     * @param yearMonth 算出対象の年月 (e.g. 201504)
     * @return 全社員の手取り額平均
     */
    public int getAverageTakeHome(int yearMonth) {
        List<EmployeeDomain> domains = findAll();

        int total = 0;
        for (EmployeeDomain domain : domains) {
            total += domain.getTakeHomeAmount(yearMonth);
        }
        int average = total / domains.size();

        return average;
    }

    /**
     * 想定年収が指定額を超えている社員数を取得する。
     *
     * @param condition 視定額
     * @return 該当する社員数
     */
    public int getCountByOverAnnualSalary(int condition) {
        List<EmployeeDomain> domains = findAll();

        int count = 0;
        for (EmployeeDomain domain : domains) {
            if (domain.getAnnualTotalSalaryPlan() >= condition) {
                count++;
            }
        }
        return count;
    }

    /**
     * 勤続月数の最大 or 最小の社員情報を取得する。
     *
     * @param selectMax 最大を求める場合は true
     * @return 社員情報
     */
    public EmployeeDomain getByDurationMonth(boolean selectMax) {
        List<EmployeeDomain> domains = findAll();

        EmployeeDomain result = null;
        int condition;
        if (selectMax) {
            condition = 0;
        } else {
            condition = Integer.MAX_VALUE;
        }

        for (EmployeeDomain domain : domains) {
            if (selectMax) {
                if (condition < domain.getDurationMonth()) {
                    result = domain;
                    condition = domain.getDurationMonth();
                }
            } else {
                if (condition > domain.getDurationMonth()) {
                    result = domain;
                    condition = domain.getDurationMonth();
                }
            }
        }

        return result;
    }

    public void setDao(EmployeeDao dao) {
        this.dao = dao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setCapabilityDao(CapabilityDao capabilityDao) {
        this.capabilityDao = capabilityDao;
    }
}

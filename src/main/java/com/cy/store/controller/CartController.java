package com.cy.store.controller;

import com.cy.store.service.ICartService;
import com.cy.store.util.JsonResult;
import com.cy.store.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("carts")
public class CartController extends BaseController {
    @Autowired
    private ICartService cartService;

    @RequestMapping("add_to_cart")
    public JsonResult<Void> addToCart(Integer pid, Integer amount, HttpSession session) {
        Integer uid = getUidFromSession(session);
        String username = getUsernameFromSession(session);
        cartService.addToCart(uid, pid, amount, username);
        return new JsonResult<Void>(OK);
    }

    @GetMapping({"", "/"})
    public JsonResult<List<CartVO>> getVOByUid(HttpSession session) {
        Integer uid = getUidFromSession(session);
        List<CartVO> data = cartService.getVOByUid(uid);
        return new JsonResult<List<CartVO>>(OK, data);
    }

    @RequestMapping("{cid}/num/add")
    public JsonResult<Integer> addNum(@PathVariable("cid") Integer cid, HttpSession session) {
        Integer uid = getUidFromSession(session);
        String username = getUsernameFromSession(session);
        Integer data = cartService.addNum(cid, uid, username);
        return new JsonResult<Integer>(OK, data);
    }
    @RequestMapping("{cid}/num/decrease")
    public JsonResult<Integer> decreaseNum(@PathVariable("cid") Integer cid, HttpSession session) {
        Integer uid = getUidFromSession(session);
        String username = getUsernameFromSession(session);
        Integer data = cartService.decreaseNum(cid, uid, username);
        return new JsonResult<Integer>(OK, data);
    }

    @GetMapping("list")
    public JsonResult<List<CartVO>> getVOByCids(Integer[] cids, HttpSession session) {
        Integer uid = getUidFromSession(session);
        List<CartVO> data = cartService.getVOByCids(uid, cids);
        return  new JsonResult<>(OK, data);
    }
}

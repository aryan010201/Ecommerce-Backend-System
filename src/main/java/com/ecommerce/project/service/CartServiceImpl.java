package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;



    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //find existing cart or create one
        Cart cart = createCart();

        //retrieve product details
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        //perform validations

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );

        if(cartItem!=null){
            throw new APIException("Product"+product.getProductName()+" already exists in the cart");
        }

        if(product.getQuantity()==0){
            throw new APIException("Product"+product.getProductName()+" is not available");
        }

        if(product.getQuantity()<quantity){
            throw new APIException("Product"+product.getProductName()+" quantity less than "+quantity);
        }
        //create cart item
        CartItem newCartItem=new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getPrice());

        cart.getCartItems().add(newCartItem);

        //save cart item
        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());// not reducing stock if product is added to cart
        cart.setTotalPrice(cart.getTotalPrice()+ (product.getPrice()*quantity));
        cartRepository.save(cart);

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems=cart.getCartItems();
        Stream<ProductDTO> productStream=cartItems.stream().map(item->{
            ProductDTO map=modelMapper.map(item.getProduct(), ProductDTO.class);
                map.setQuantity(item.getQuantity());
                return map;
        });
        //return updated cart
        cartDTO.setProducts(productStream.toList());


        return cartDTO;
    }
    private Cart createCart(){
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }
        Cart cart=new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();
        if(carts.size()==0){
            throw new APIException("No carts found");
        }
        List<CartDTO> cartDTOS=carts.stream()
                .map(cart->{
                    CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
                    List<ProductDTO> products=cart.getCartItems().stream()
                            .map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                            .collect(Collectors.toList());
                    cartDTO.setProducts(products);
                    return cartDTO;
                }).collect(Collectors.toList());
        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart=cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart","email",cartId);
        }
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products=cart.getCartItems().stream()
                .map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }




    @Override
    @Transactional
    public CartDTO updateProductQauntityInCart(Long productId, Integer quantity) {
        String emailId=authUtil.loggedInEmail();
        Cart userCart=cartRepository.findCartByEmail(emailId);
        Long cartId=userCart.getCartId();
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));
        if(cart==null){
            throw new ResourceNotFoundException("Cart","email",authUtil.loggedInEmail());
        }

        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        if(product.getQuantity()==0){
            throw new APIException("Product"+product.getProductName()+" is not available");
        }

        if(product.getQuantity()<quantity){
            throw new APIException("Product"+product.getProductName()+" quantity less than "+quantity);
        }

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("Product"+product.getProductName()+" does not exist in the cart");
        }

        int newQuantity=cartItem.getQuantity()+quantity;

        if(newQuantity<0){
            throw new APIException("the quantity cannot be less than 0");
        }

        if(newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }
        else{
            cartItem.setProductPrice(product.getPrice());
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice()+(cartItem.getProductPrice()*quantity));
            cartRepository.save(cart);
        }


        CartItem updatedItem=cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems=cart.getCartItems();

        Stream<ProductDTO> productStream=cartItems.stream().map(
                item->{
                    ProductDTO prd=modelMapper.map(item.getProduct(),ProductDTO.class);
                    prd.setQuantity(item.getQuantity());
                    return prd;
                }
        );
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }


    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new ResourceNotFoundException("Product","productId",productId);
        }

        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product"+cartItem.getProduct().getProductName()+" has been deleted";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart=cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));
        Product product=productRepository.findById(productId)
                .orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem==null){
            throw new APIException("Product"+product.getProductName()+" does not exist in the cart");
        }

        double cartPrice=cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));

        cartItem=cartItemRepository.save(cartItem);
    }

}

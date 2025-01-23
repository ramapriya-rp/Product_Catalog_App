import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import debounce from 'lodash.debounce';
import {
  TextField,
  Typography,
  Container,
  Pagination,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  SelectChangeEvent,
  Box,
  IconButton,
  InputAdornment,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import '../index.css'; // Use index.css for styles

interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  imageUrl: string;
  description: string;
}

const ProductList: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [errorMessage, setErrorMessage] = useState('');

  const fetchProducts = useCallback(async (pageNo: number, size: number) => {
    try {
      console.log(`Fetching products for page: ${pageNo}, size: ${size}`);
      const response = await axios.get(`http://localhost:8080/products?pageNo=${pageNo - 1}&pageSize=${size}`);
      if (response.data && response.data.products) {
        setProducts(response.data.products);
        setTotalPages(response.data.totalPages);
        setErrorMessage('');
      } else {
        setProducts([]);
        setTotalPages(1);
        setErrorMessage('No products found.');
      }
    } catch (error) {
      console.error('Error fetching products:', error);
      setProducts([]);
      setTotalPages(1);
      setErrorMessage('Failed to load products.');
    }
  }, []);

  const handleSearch = useCallback(
    debounce(async (query: string) => {
      try {
        if (!query.trim()) {
          setSearchTerm('');
          fetchProducts(1, pageSize);
          return;
        }
        const response = await axios.get(`http://localhost:8080/search?query=${query}`);
        if (response.data && Array.isArray(response.data)) {
          setProducts(response.data);
          setTotalPages(1);
          setErrorMessage('');
        } else {
          setProducts([]);
          setTotalPages(1);
          setErrorMessage('No products found for your search.');
        }
      } catch (error) {
        console.error('Error during search:', error);
        setProducts([]);
        setTotalPages(1);
        setErrorMessage('Search failed. Please try again later.');
      }
    }, 300),
    [pageSize, fetchProducts]
  );

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = event.target.value;
    setSearchTerm(value);
    handleSearch(value);
  };

  const handleClearSearch = () => {
    setSearchTerm('');
    fetchProducts(1, pageSize); // Reset to default product list
  };

  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);
    fetchProducts(value, pageSize);
  };

  const handlePageSizeChange = (event: SelectChangeEvent<number>) => {
    const newSize = Number(event.target.value);
    setPageSize(newSize);
    setPage(1);
    fetchProducts(1, newSize);
  };

  useEffect(() => {
    fetchProducts(page, pageSize);
  }, [page, pageSize, fetchProducts]);

  return (
    <Container className="styled-container">
      <Box display="flex" justifyContent="space-between" alignItems="center">
        <Box flex={1} mr={2}>
          <TextField
            label="Search products..."
            variant="outlined"
            fullWidth
            value={searchTerm}
            onChange={handleChange}
            className="styled-search-bar"
            InputProps={{
              endAdornment: searchTerm && (
                <InputAdornment position="end">
                  <IconButton onClick={handleClearSearch} edge="end">
                    <CloseIcon />
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        </Box>
        <Box display="flex" alignItems="center">
          <FormControl variant="outlined" className="styled-form-control">
            <InputLabel>Items per page</InputLabel>
            <Select value={pageSize} onChange={handlePageSizeChange} label="Items per page">
              <MenuItem value={5}>5</MenuItem>
              <MenuItem value={10}>10</MenuItem>
              <MenuItem value={15}>15</MenuItem>
            </Select>
          </FormControl>
          <Pagination count={totalPages} page={page} onChange={handlePageChange} className="styled-pagination" />
        </Box>
      </Box>
      <Box mt={3}>
        {errorMessage ? (
          <Typography variant="h6" color="error" className="styled-error-message">
            {errorMessage}
          </Typography>
        ) : products.length > 0 ? (
          <>
            {/* Table Headers */}
            <Box className="styled-table-headers">
              <Box flex={4} textAlign="left" >
                Name
              </Box>
              <Box flex={4} textAlign="left">
                Category
              </Box>
              <Box flex={2} textAlign="left">
                Price
              </Box>
            </Box>
            {/* Product List */}
            {products.map((product) => (
              <Box key={product.id} className="styled-table-row">
                <Box flex={4}>
                  <Typography variant="h6" className="product-name">
                    <Link
                      to={`/products/${product.id}`}
                      state={{ product }}
                      style={{ textDecoration: 'none', color: 'inherit' }}
                    >
                      {product.name}
                    </Link>
                  </Typography>
                </Box>
                <Box flex={4}>
                  <Typography variant="body2" color="textSecondary">
                    {product.category}
                  </Typography>
                </Box>
                <Box flex={2}>
                  <Typography variant="h6" color="primary">
                    {product.price} SEK
                  </Typography>
                </Box>
              </Box>
            ))}
          </>
        ) : (
          <Typography variant="h6" className="styled-error-message">
            No products available
          </Typography>
        )}
      </Box>
    </Container>
  );
};

export default ProductList;
